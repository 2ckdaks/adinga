import axios, { AxiosHeaders, InternalAxiosRequestConfig } from "axios";

const baseURL = process.env.EXPO_PUBLIC_API_BASE_URL?.replace(/\/+$/, "");
const DEV_TOKEN = process.env.EXPO_PUBLIC_DEV_TOKEN ?? "";

if (!baseURL) {
  console.warn("[API] EXPO_PUBLIC_API_BASE_URL is empty!");
} else {
  console.log("[API] baseURL =", baseURL);
}
if (!DEV_TOKEN) {
  console.warn("[API] EXPO_PUBLIC_DEV_TOKEN is empty! (Authorization 미첨부)");
}

export const api = axios.create({
  baseURL,
  timeout: 10000,
  headers: { "Content-Type": "application/json" },
});

/** 필요 시 런타임에 토큰 바꿀 수 있도록 헬퍼 제공 */
let runtimeToken = DEV_TOKEN;
export function setDevToken(token: string) {
  runtimeToken = token;
}

/** 요청 인터셉터: Authorization 자동 첨부 */
api.interceptors.request.use((cfg: InternalAxiosRequestConfig) => {
  const method = cfg.method?.toUpperCase();
  const url = (cfg.baseURL ?? "") + (cfg.url ?? "");
  console.log("[REQ]", method, url);

  // Authorization이 비어있고 토큰이 있으면 세팅
  const hasAuthHeader =
    (cfg.headers as AxiosHeaders)?.has?.("Authorization") ||
    // 혹시 AxiosHeaders가 아닌 환경(구버전) 대비
    !!(cfg.headers as any)?.Authorization;

  if (!hasAuthHeader && runtimeToken) {
    // headers가 없으면 생성
    if (!cfg.headers) cfg.headers = new AxiosHeaders();

    // Axios v1: AxiosHeaders 인스턴스면 set() 사용
    if (cfg.headers instanceof AxiosHeaders) {
      cfg.headers.set("Authorization", `Bearer ${runtimeToken}`);
    } else {
      // 혹시 모를 타입 케이스(웹/네이티브 번들러 차이) 대비
      (cfg.headers as any).Authorization = `Bearer ${runtimeToken}`;
    }
  }

  return cfg;
});

/** 응답/에러 로그 */
api.interceptors.response.use(
  (res) => {
    console.log("[RES]", res.status, res.config.url);
    return res;
  },
  (err) => {
    console.log("[ERR]", err?.message, err?.config?.url);
    throw err;
  }
);
