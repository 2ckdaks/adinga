import { api } from "./client";
import { GeoRule } from "./todos";

/** 특정 todo에 규칙 생성 */
export async function createRuleForTodo(params: {
  todoId: number;
  deviceId: string;
  lat: number;
  lng: number;
  radiusM: number;
  when: "ENTER" | "EXIT";
  enabled?: boolean;
}) {
  const { data } = await api.post("/api/triggers/rules", {
    enabled: true,
    ...params,
  });
  return data as GeoRule & { id: number };
}

export async function updateRule(
  id: number,
  patch: Partial<GeoRule> & { enabled?: boolean }
) {
  const { data } = await api.patch(`/api/triggers/rules/${id}`, patch);
  return data as GeoRule & { id: number };
}

export async function getRule(id: number) {
  const { data } = await api.get(`/api/triggers/rules/${id}`);
  return data as GeoRule & { id: number };
}

/** todoId 로 규칙 조회 (없으면 404) */
export async function getRuleByTodo(todoId: number) {
  const { data } = await api.get(`/api/triggers/rules/by-todo/${todoId}`);
  return data as GeoRule & { id: number };
}
