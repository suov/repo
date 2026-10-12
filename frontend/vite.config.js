import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    host: "127.0.0.1",
    port: 5500,
    strictPort: true,
    proxy: {
      "/authenticate": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/persona": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/usuario": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
