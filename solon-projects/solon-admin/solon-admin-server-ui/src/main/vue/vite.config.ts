import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite';
import {ArcoResolver} from 'unplugin-vue-components/resolvers';
import {vitePluginForArco} from '@arco-plugins/vite-vue'

// https://vitejs.dev/config/
export default defineConfig({
    define: {
        __VUE_I18N_FULL_INSTALL__: true,
        __VUE_I18N_LEGACY_API__: false,
        __INTLIFY_PROD_DEVTOOLS__: false,
    },
    base: './',
    plugins: [
        vue(),
        vitePluginForArco({
            style: 'css'
        }),
        AutoImport({
            resolvers: [ArcoResolver()],
        }),
        Components({
            resolvers: [
                ArcoResolver({
                    sideEffect: true
                }),
                (componentName) => {
                    if (componentName == "VChart") {
                        return {
                            from: "vue-echarts"
                        }
                    }
                }
            ],
        })
    ],
    build: {
        target: "es2020",
        outDir: "../../../target/dist",
        sourcemap: true,
        minify: false
    }
})
