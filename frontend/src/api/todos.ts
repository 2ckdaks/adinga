import { api } from "./client";

export type GeoRule = {
  id?: number;
  todoId?: number;
  lat: number;
  lng: number;
  radiusM: number;
  when: "ENTER" | "EXIT";
  deviceId: string;
  enabled: boolean;
};

export type Todo = {
  id: number;
  title: string;
  memo?: string;
  completed: boolean;
  enabled?: boolean; // UI 표시용
  rule?: GeoRule | null; // 서버가 붙여줄 수도 있음
  createdAt?: string;
  updatedAt?: string;
};

export async function listTodos(): Promise<Todo[]> {
  const { data } = await api.get("/api/todos", { params: { size: 100 } });
  return (data.content ?? []) as Todo[];
}

export async function getTodo(id: number): Promise<Todo> {
  const { data } = await api.get(`/api/todos/${id}`);
  return data as Todo;
}

export async function addTodo(title: string): Promise<Todo> {
  const { data } = await api.post("/api/todos", { title });
  return data as Todo;
}

export async function updateTodo(
  id: number,
  patch: Partial<Pick<Todo, "title" | "completed">>
): Promise<Todo> {
  const { data } = await api.patch(`/api/todos/${id}`, patch);
  return data as Todo;
}

export async function toggleTodo(id: number): Promise<void> {
  await api.patch(`/api/todos/${id}/toggle`);
}

export async function removeTodo(id: number): Promise<void> {
  await api.delete(`/api/todos/${id}`);
}
