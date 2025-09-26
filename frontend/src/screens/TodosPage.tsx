import { useEffect, useState, useCallback } from "react";
import {
  View,
  Text,
  FlatList,
  ActivityIndicator,
  Pressable,
  TextInput,
  Alert,
  RefreshControl,
  Switch,
} from "react-native";
import { useFocusEffect } from "@react-navigation/native";
import { router } from "expo-router";
import {
  listTodos,
  addTodo,
  toggleTodo,
  removeTodo,
  Todo,
} from "@/src/api/todos";
import { updateRule } from "@/src/api/triggers";

export default function TodosPage() {
  const [items, setItems] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [title, setTitle] = useState("");

  const load = useCallback(async () => {
    try {
      setLoading(true);
      const data = await listTodos();
      setItems(data.map((t) => ({ enabled: true, ...t })));
    } catch (e: any) {
      Alert.alert("불러오기 실패", e?.message ?? "unknown");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  useFocusEffect(
    useCallback(() => {
      load();
    }, [load])
  );

  const onRefresh = async () => {
    try {
      setRefreshing(true);
      const data = await listTodos();
      setItems(data.map((t) => ({ enabled: true, ...t })));
    } catch (e: any) {
      Alert.alert("새로고침 실패", e?.message ?? "unknown");
    } finally {
      setRefreshing(false);
    }
  };

  const onAdd = async () => {
    const t = title.trim();
    if (!t) return;
    try {
      await addTodo(t);
      setTitle("");
      await load();
    } catch (e: any) {
      Alert.alert("추가 실패", e?.message ?? "unknown");
    }
  };

  const onToggleCompleted = async (id: number) => {
    try {
      await toggleTodo(id);
      setItems((prev) =>
        prev.map((it) =>
          it.id === id ? { ...it, completed: !it.completed } : it
        )
      );
    } catch (e: any) {
      Alert.alert("완료 토글 실패", e?.message ?? "unknown");
    }
  };

  const onToggleEnabled = async (item: Todo, next: boolean) => {
    try {
      if (item.rule?.id) {
        await updateRule(item.rule.id, { enabled: next });
      } else {
        Alert.alert(
          "지오펜스 없음",
          "이 할 일에는 위치 규칙이 없습니다.\n상세 화면에서 위치/반경을 먼저 설정해 주세요."
        );
      }
      setItems((prev) =>
        prev.map((it) => (it.id === item.id ? { ...it, enabled: next } : it))
      );
    } catch (e: any) {
      Alert.alert("알림 ON/OFF 실패", e?.message ?? "unknown");
    }
  };

  const onDelete = async (id: number) => {
    try {
      await removeTodo(id);
      setItems((prev) => prev.filter((it) => it.id !== id));
    } catch (e: any) {
      Alert.alert("삭제 실패", e?.message ?? "unknown");
    }
  };

  const openDetail = (item: Todo) => {
    router.push(`/todos/${item.id}`);
  };

  if (loading) {
    return (
      <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
        <ActivityIndicator />
        <Text style={{ marginTop: 8 }}>Loading...</Text>
      </View>
    );
  }

  return (
    <View style={{ flex: 1, padding: 16, gap: 16 }}>
      {/* 입력 + 추가 */}
      <View style={{ flexDirection: "row", gap: 8 }}>
        <TextInput
          style={{
            flex: 1,
            borderWidth: 1,
            borderRadius: 8,
            paddingHorizontal: 12,
            paddingVertical: 10,
          }}
          placeholder="새 할 일..."
          value={title}
          onChangeText={setTitle}
          onSubmitEditing={onAdd}
          returnKeyType="done"
        />
        <Pressable
          onPress={onAdd}
          style={{
            backgroundColor: "#2563eb",
            borderRadius: 8,
            paddingHorizontal: 16,
            justifyContent: "center",
          }}
        >
          <Text style={{ color: "white", fontWeight: "600" }}>추가</Text>
        </Pressable>
      </View>

      {/* 목록 */}
      <FlatList
        data={items}
        keyExtractor={(it) => String(it.id)}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        ItemSeparatorComponent={() => (
          <View style={{ height: 1, backgroundColor: "#e5e7eb" }} />
        )}
        renderItem={({ item }) => (
          <View
            style={{
              paddingVertical: 12,
              paddingHorizontal: 8,
              flexDirection: "row",
              alignItems: "center",
              gap: 12,
            }}
          >
            {/* 좌측: 제목/메모 (탭=상세, 롱프레스=완료 토글) */}
            <Pressable
              onPress={() => openDetail(item)}
              onLongPress={() => onToggleCompleted(item.id)}
              style={{ flex: 1, paddingRight: 12 }}
            >
              <Text
                style={{
                  fontWeight: "600",
                  textDecorationLine: item.completed ? "line-through" : "none",
                  color: item.completed ? "#9ca3af" : "#111827",
                }}
              >
                {item.title}
              </Text>
              {!!item.memo && (
                <Text
                  numberOfLines={1}
                  style={{ color: "#6b7280", marginTop: 2 }}
                >
                  {item.memo}
                </Text>
              )}
            </Pressable>

            {/* 알림 스위치 */}
            <Switch
              value={item.enabled ?? true}
              onValueChange={(v) => onToggleEnabled(item, v)}
            />

            {/* 삭제 버튼 */}
            <Pressable
              onPress={() => onDelete(item.id)}
              style={{
                paddingVertical: 6,
                paddingHorizontal: 10,
                borderWidth: 1,
                borderRadius: 8,
              }}
            >
              <Text>삭제</Text>
            </Pressable>
          </View>
        )}
        ListEmptyComponent={
          <View style={{ paddingVertical: 24, alignItems: "center" }}>
            <Text style={{ color: "#6b7280" }}>등록된 할 일이 없어요.</Text>
          </View>
        }
      />
    </View>
  );
}
