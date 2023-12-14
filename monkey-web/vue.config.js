

const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  // 支持内网穿透配置
  devServer: {
    historyApiFallback: true,
    allowedHosts: "all"
  },

  transpileDependencies: true,
  
  chainWebpack: config => {
      config.module
      .rule('css')
      .test(/\.css$/)
      .oneOf('vue')
      .resourceQuery(/\?vue/)
      .use('px2rem')
      .loader('px2rem-loader')
      .options({
      remUnit: 75
      })
    },
    lintOnSave: false,
  configureWebpack: {
    resolve: {
      fallback: {
        "timers": require.resolve("timers-browserify")
      }
    }
  },
  
})

