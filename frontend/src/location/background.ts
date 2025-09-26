import * as TaskManager from "expo-task-manager";
import * as Location from "expo-location";
import { uploadLocations } from "@/src/api/locations";
import { getDeviceId } from "@/src/utils/deviceId";

const TASK = "adinga.location";

TaskManager.defineTask(TASK, async ({ data, error }) => {
  if (error) return;
  const { locations } = data as any;
  if (!locations?.length) return;

  const deviceId = await getDeviceId();

  const batch = locations.map((l: any) => ({
    deviceId,
    lat: l.coords.latitude,
    lng: l.coords.longitude,
    ts: l.timestamp ?? Date.now(),
  }));

  try {
    await uploadLocations(batch);
  } catch (e) {
    // 네트워크 없을 때 등은 조용히 무시(재시도 전략은 추후)
    console.log("[bg] upload failed", e);
  }
});

export async function startBackgroundLocation() {
  const fg = await Location.requestForegroundPermissionsAsync();
  const bg = await Location.requestBackgroundPermissionsAsync();
  if (fg.status !== "granted" || bg.status !== "granted")
    throw new Error("위치 권한 필요");

  await Location.startLocationUpdatesAsync(TASK, {
    accuracy: Location.Accuracy.Balanced,
    timeInterval: 60_000,
    distanceInterval: 50,
    showsBackgroundLocationIndicator: false,
    pausesUpdatesAutomatically: true,
  });
}
