import * as Crypto from "expo-crypto";
import AsyncStorage from "@react-native-async-storage/async-storage";

const K = "adinga.deviceId";

export async function getDeviceId(): Promise<string> {
  const saved = await AsyncStorage.getItem(K);
  if (saved) return saved;

  const candidate = (Crypto as any).getRandomUUIDAsync
    ? await (Crypto as any).getRandomUUIDAsync()
    : (
        await Crypto.digestStringAsync(
          Crypto.CryptoDigestAlgorithm.SHA256,
          String(Date.now() + Math.random())
        )
      ).slice(0, 32);

  await AsyncStorage.setItem(K, candidate);
  return candidate;
}
