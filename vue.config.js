module.exports = {
  transpileDependencies: ["vuetify"],
  devServer: {
    proxy: "http://back.atgcom.com"
  }
};
