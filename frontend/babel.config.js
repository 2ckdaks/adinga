module.exports = function (api) {
  api.cache(true);
  return {
    presets: ["babel-preset-expo"],
    plugins: [
      require.resolve("expo-router/babel"),
      "react-native-worklets/plugin", // RN 0.81+에서 reanimated 플러그인 대체
    ],
  };
};
