const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  publicPath: './',
  devServer: {
    client: {
      overlay: false
    },
    allowedHosts: 'all',
    host: "0.0.0.0", //指定使用一个 host。默认是 localhost，这里默认值即可
    port: 8082, //指定端口
    hot: true, // 开启热更新
    https: false, // 是否开启https模式
    proxy: { // 请求代理服务器
      '/api': { //带上api前缀的
        target: process.env.VUE_APP_BASE_API, //代理目标地址
        changeOrigin: true,
        secure: false,
        pathRewrite: { // 在发出请求后将/api替换为''空值，这样不影响接口请求
          // '^/api': ''
        }
      },
    }
  }
})
