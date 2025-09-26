// app/_layout.tsx
import { Stack } from "expo-router";
import "react-native-get-random-values";

export default function Layout() {
  return (
    <Stack
      screenOptions={{
        headerBackTitle: "할 일",
      }}
    >
      <Stack.Screen name="index" options={{ title: "할 일" }} />
      <Stack.Screen name="todos/[id]" options={{ title: "할 일 상세" }} />
    </Stack>
  );
}
