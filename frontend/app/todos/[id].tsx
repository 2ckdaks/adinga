import { useLocalSearchParams, router } from "expo-router";
import { useEffect, useState, useMemo } from "react";
import { SafeAreaView } from "react-native-safe-area-context";
import {
  KeyboardAvoidingView,
  Platform,
  Text,
  TextInput,
  View,
  Pressable,
  Switch,
  Alert,
  FlatList,
} from "react-native";
import { GooglePlacesAutocomplete } from "react-native-google-places-autocomplete";
import { api } from "@/src/api/client";
import { Todo } from "@/src/api/todos";
import {
  createRuleForTodo,
  updateRule,
  getRuleByTodo,
  getRule,
} from "@/src/api/triggers";
import * as Location from "expo-location";
import { getDeviceId } from "@/src/utils/deviceId";

const PLACES_KEY = process.env.EXPO_PUBLIC_GOOGLE_PLACES_KEY!;

export default function TodoDetail() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const todoId = useMemo(() => Number(id), [id]);

  // ── 할 일 필드
  const [title, setTitle] = useState("");
  const [completed, setCompleted] = useState(false);

  // ── 위치 규칙 필드
  const [enabled, setEnabled] = useState(true);
  const [lat, setLat] = useState<number | undefined>();
  const [lng, setLng] = useState<number | undefined>();
  const [radiusM, setRadiusM] = useState<string>("200");
  const [when, setWhen] = useState<"ENTER" | "EXIT">("ENTER");
  const [ruleId, setRuleId] = useState<number | undefined>();

  // 현재 위치(바이어스용)
  const [curLat, setCurLat] = useState<number | undefined>();
  const [curLng, setCurLng] = useState<number | undefined>();

  // 실제 deviceId
  const [deviceId, setDeviceId] = useState<string | null>(null);

  // 편의 변수
  const disabled = !enabled;

  const load = async () => {
    // 1) 할 일
    const { data } = await api.get(`/api/todos/${todoId}`);
    const t = data as Todo;
    setTitle(t.title ?? "");
    setCompleted(!!t.completed);

    // 2) 규칙: 우선 Todo에 딸려온 rule, 없으면 서버로 재조회
    let rule = t.rule as any | null;
    if (!rule) {
      try {
        rule = await getRuleByTodo(todoId); // 404면 catch로 감
      } catch {
        rule = null;
      }
    } else if (!rule.id && rule.todoId) {
      try {
        rule = await getRule(rule.todoId);
      } catch {}
    }

    if (rule) {
      setRuleId(rule.id ?? rule.todoId);
      setEnabled(rule.enabled ?? true);
      setLat(rule.lat);
      setLng(rule.lng);
      setWhen(rule.when);
      setRadiusM(String(rule.radiusM ?? 200));
    } else {
      // 규칙이 없으면 기본값
      setRuleId(undefined);
      setEnabled(true);
      setLat(undefined);
      setLng(undefined);
      setWhen("ENTER");
      setRadiusM("200");
    }
  };

  useEffect(() => {
    (async () => {
      try {
        setDeviceId(await getDeviceId());
        const perm = await Location.requestForegroundPermissionsAsync();
        if (perm.status === "granted") {
          const cur = await Location.getCurrentPositionAsync({});
          setCurLat(cur.coords.latitude);
          setCurLng(cur.coords.longitude);
        }
      } catch (e) {
        console.log("[location] failed:", e);
      }
    })();
  }, []);

  useEffect(() => {
    if (!Number.isFinite(todoId)) return;
    load();
  }, [todoId]);

  /** 저장 버튼: 할 일 저장 → 규칙 생성/수정 */
  const onSaveAll = async () => {
    try {
      // 1) 할 일 저장
      await api.patch(`/api/todos/${todoId}`, { title, completed });

      // 2) 규칙 처리
      if (disabled) {
        if (ruleId) await updateRule(ruleId, { enabled: false });
      } else {
        const r = Number(radiusM);
        if (!lat || !lng) {
          Alert.alert("위치 필요", "장소를 먼저 선택해 주세요.");
          return;
        }
        if (!r || r <= 0) {
          Alert.alert("반경 오류", "반경(미터)을 바르게 입력해 주세요.");
          return;
        }
        if (!deviceId) {
          Alert.alert("기기 식별 필요", "잠시 후 다시 시도해 주세요.");
          return;
        }

        if (ruleId) {
          await updateRule(ruleId, {
            lat,
            lng,
            radiusM: r,
            when,
            enabled: true,
          });
        } else {
          const created = await createRuleForTodo({
            todoId,
            deviceId,
            lat,
            lng,
            radiusM: r,
            when,
            enabled: true,
          });
          setRuleId(created.id);
        }
      }

      Alert.alert("저장됨", "변경사항이 저장되었습니다.");
      router.back();
    } catch (e: any) {
      const status = e?.response?.status;
      if (status === 404) {
        Alert.alert("규칙 없음", "상세 정보를 다시 열어 규칙을 생성해 주세요.");
      } else {
        Alert.alert("저장 실패", e?.message ?? "unknown");
      }
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        keyboardVerticalOffset={Platform.OS === "ios" ? 8 : 0}
      >
        <FlatList
          data={[{ key: "form" }]}
          keyExtractor={(it) => it.key}
          keyboardShouldPersistTaps="handled"
          contentContainerStyle={{ padding: 16, gap: 16, paddingBottom: 32 }}
          renderItem={() => (
            <View style={{ gap: 16 }}>
              {/* === 할 일 === */}
              <Text style={{ fontWeight: "700" }}>할 일</Text>
              <TextInput
                style={{
                  borderWidth: 1,
                  borderRadius: 8,
                  paddingHorizontal: 12,
                  paddingVertical: 10,
                }}
                value={title}
                onChangeText={setTitle}
                placeholder="제목"
              />
              <View
                style={{ flexDirection: "row", alignItems: "center", gap: 12 }}
              >
                <Text>완료</Text>
                <Switch value={completed} onValueChange={setCompleted} />
              </View>

              <View
                style={{
                  height: 1,
                  backgroundColor: "#e5e7eb",
                  marginVertical: 8,
                }}
              />

              {/* === 위치 규칙 === */}
              <Text style={{ fontWeight: "700" }}>위치 규칙</Text>
              <View
                style={{ flexDirection: "row", alignItems: "center", gap: 12 }}
              >
                <Text>활성화</Text>
                <Switch value={enabled} onValueChange={setEnabled} />
              </View>

              {/* 검색 박스 */}
              <View
                style={{
                  position: "relative",
                  ...(disabled ? { opacity: 0.5 } : null),
                }}
                pointerEvents={disabled ? "none" : "auto"}
              >
                <GooglePlacesAutocomplete
                  placeholder="장소 검색(건물/상호명 등)"
                  fetchDetails
                  onPress={(data, details) => {
                    const loc = details?.geometry?.location;
                    if (loc?.lat && loc?.lng) {
                      setLat(loc.lat);
                      setLng(loc.lng);
                    }
                  }}
                  query={{
                    key: PLACES_KEY,
                    language: "ko",
                    components: "country:kr",
                    ...(curLat && curLng
                      ? { location: `${curLat},${curLng}`, radius: 3000 }
                      : {}),
                  }}
                  nearbyPlacesAPI="GooglePlacesSearch"
                  GooglePlacesSearchQuery={
                    curLat && curLng
                      ? { location: `${curLat},${curLng}`, rankby: "distance" }
                      : undefined
                  }
                  minLength={2}
                  debounce={200}
                  textInputProps={{ returnKeyType: "search" }}
                  onFail={(err) =>
                    Alert.alert("검색 오류", String(err?.message ?? err))
                  }
                  onNotFound={() =>
                    Alert.alert("검색 결과 없음", "다른 키워드를 입력해 보세요")
                  }
                  enablePoweredByContainer={false}
                  predefinedPlaces={[]}
                  styles={{
                    container: { zIndex: 20 },
                    textInput: {
                      borderWidth: 1,
                      borderRadius: 8,
                      paddingHorizontal: 12,
                      paddingVertical: Platform.OS === "ios" ? 12 : 8,
                    },
                    listView: {
                      position: "absolute",
                      top: 48,
                      left: 0,
                      right: 0,
                      backgroundColor: "white",
                      borderWidth: 1,
                      borderRadius: 8,
                      zIndex: 30,
                      elevation: 30,
                      maxHeight: 240,
                    },
                  }}
                />
              </View>

              {/* 좌표/반경/ENTER·EXIT */}
              <View style={{ gap: 10, marginTop: 12 }}>
                <TextInput
                  placeholder="위도(lat)"
                  value={lat ? String(lat) : ""}
                  editable={false}
                  style={{
                    borderWidth: 1,
                    borderRadius: 8,
                    padding: 10,
                    backgroundColor: "#f3f4f6",
                  }}
                />
                <TextInput
                  placeholder="경도(lng)"
                  value={lng ? String(lng) : ""}
                  editable={false}
                  style={{
                    borderWidth: 1,
                    borderRadius: 8,
                    padding: 10,
                    backgroundColor: "#f3f4f6",
                  }}
                />
                <TextInput
                  placeholder="반경(m)"
                  value={radiusM}
                  onChangeText={setRadiusM}
                  editable={!disabled}
                  inputMode="numeric"
                  style={{
                    borderWidth: 1,
                    borderRadius: 8,
                    padding: 10,
                    ...(disabled ? { backgroundColor: "#f9fafb" } : null),
                  }}
                />
                <View style={{ flexDirection: "row", gap: 8 }}>
                  <Pressable
                    disabled={disabled}
                    onPress={() => setWhen("ENTER")}
                    style={{
                      flex: 1,
                      borderWidth: 1,
                      borderRadius: 10,
                      padding: 12,
                      alignItems: "center",
                      backgroundColor: when === "ENTER" ? "#e0e7ff" : "white",
                      ...(disabled ? { opacity: 0.5 } : null),
                    }}
                  >
                    <Text>ENTER</Text>
                  </Pressable>
                  <Pressable
                    disabled={disabled}
                    onPress={() => setWhen("EXIT")}
                    style={{
                      flex: 1,
                      borderWidth: 1,
                      borderRadius: 10,
                      padding: 12,
                      alignItems: "center",
                      backgroundColor: when === "EXIT" ? "#e0e7ff" : "white",
                      ...(disabled ? { opacity: 0.5 } : null),
                    }}
                  >
                    <Text>EXIT</Text>
                  </Pressable>
                </View>
              </View>

              {/* 저장 버튼 */}
              <Pressable
                onPress={onSaveAll}
                style={{
                  backgroundColor: "#2563eb",
                  borderRadius: 8,
                  padding: 14,
                  alignItems: "center",
                  marginTop: 8,
                }}
              >
                <Text style={{ color: "white", fontWeight: "600" }}>저장</Text>
              </Pressable>
            </View>
          )}
        />
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
