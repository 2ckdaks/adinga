import { api } from "./client";

export type RecentNotification = {
  ruleId: number | null;
  ruleName: string;
  occurredAt: string;
};

export async function listRecent(): Promise<RecentNotification[]> {
  const { data } = await api.get("/api/notifications/recent");
  return data as RecentNotification[];
}

export async function clearRecent(): Promise<void> {
  await api.delete("/api/notifications/recent");
}

export async function getRecent(): Promise<RecentNotification[]> {
  const { data } = await api.get("/api/notifications/recent");
  return data as RecentNotification[];
}

export async function registerPushToken(
  deviceId: string,
  expoPushToken: string
) {
  await api.post("/api/notifications/tokens", {
    deviceId,
    expoToken: expoPushToken,
  });
}

export async function unregisterPushToken(deviceId: string) {
  await api.delete(`/api/notifications/tokens/${encodeURIComponent(deviceId)}`);
}
