import * as Notifications from "expo-notifications";
import * as Device from "expo-device";
import { registerPushToken } from "@/src/api/notifications";

export async function ensurePushRegistered(deviceId: string) {
  if (!Device.isDevice) return;
  const { status: existing } = await Notifications.getPermissionsAsync();
  let final = existing;
  if (existing !== "granted") {
    const { status } = await Notifications.requestPermissionsAsync();
    final = status;
  }
  if (final !== "granted") throw new Error("푸시 권한 필요");
  const token = (await Notifications.getExpoPushTokenAsync()).data;
  await registerPushToken(deviceId, token);
}
