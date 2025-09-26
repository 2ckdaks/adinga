import { useEffect } from "react";
import TodosPage from "@/src/screens/TodosPage";
import { getDeviceId } from "@/src/utils/deviceId";
import { ensurePushRegistered } from "@/src/utils/bootstrapPush";

export default function Index() {
  useEffect(() => {
    (async () => {
      try {
        const id = await getDeviceId();
        await ensurePushRegistered(id);
      } catch (e) {
        console.log("[push] skipped or failed:", e);
      }
    })();
  }, []);

  return <TodosPage />;
}
