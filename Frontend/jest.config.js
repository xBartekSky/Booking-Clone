export default {
  testEnvironment: "jest-environment-jsdom",
  transform: {
    "^.+\\.[tj]sx?$": "babel-jest",
  },
  moduleFileExtensions: ["js", "jsx", "ts", "tsx"],
  setupFilesAfterEnv: ["@testing-library/jest-dom", "<rootDir>/jest.setup.js"],
  moduleNameMapper: {
    "\\.(css|less|scss|sass)$": "identity-obj-proxy",
  },
};
