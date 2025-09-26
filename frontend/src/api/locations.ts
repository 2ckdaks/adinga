import { api } from "./client";

export type LocationEvent = {
  deviceId: string;
  lat: number;
  lng: number;
  ts: string;
};

export async function postLocation(ev: LocationEvent) {
  // 게이트웨이 경유: /api/locations + 백엔드 경로 /events/locations
  return api.post("/api/locations/events/locations", ev);
}

export async function sendLocation(
  deviceId: string,
  lat: number,
  lng: number,
  ts = new Date().toISOString()
) {
  await postLocation({ deviceId, lat, lng, ts });
}

/** 배치 업로드: 서버 배치 엔드포인트가 없으므로 프론트에서 반복 호출 */
export async function uploadLocations(
  batch: Array<{
    deviceId: string;
    lat: number;
    lng: number;
    ts: string | number;
  }>
) {
  // ts가 number(Epoch)로 오면 ISO로 바꿔서 보냄
  await Promise.all(
    batch.map((e) => {
      const tsIso =
        typeof e.ts === "number" ? new Date(e.ts).toISOString() : e.ts;
      return postLocation({
        deviceId: e.deviceId,
        lat: e.lat,
        lng: e.lng,
        ts: tsIso,
      });
    })
  );
}
