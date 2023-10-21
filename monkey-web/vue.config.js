const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  // 支持内网穿透配置
  devServer: {
    historyApiFallback: true,
    allowedHosts: "all"
  },

  transpileDependencies: true,
  lintOnSave: false,
  configureWebpack: {
    resolve: {
      fallback: {
        "timers": require.resolve("timers-browserify")
      }
    }
  },
  
})
