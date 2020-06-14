<template>
  <v-app>
    <navigation v-if="$store.state.user.authenticated" />
    <router-view />
    <v-snackbar v-model="$store.state.snackbar.isVisible" :multi-line="true">
      {{ $store.state.snackbar.text }}
      <v-btn color="red" text @click="$store.commit('overlay', false)">
        Close
      </v-btn>
    </v-snackbar>
    <v-overlay :value="$store.state.overlay">
      <v-progress-circular indeterminate size="64"></v-progress-circular>
    </v-overlay>
  </v-app>
</template>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.v-toolbar .logo {
  margin-right: -2px;
}
.v-btn {
  text-transform: capitalize;
}
.secondary-btn {
  background: #fff !important;
  box-shadow: none !important;
  border: 1px solid #1776d3 !important;
  color: #1776d3 !important;
  font-weight: bold;
  &.v-btn--disabled {
    border: 1px solid #b9b9b9 !important;
  }
}
.main-btn {
  font-weight: bold;
}
</style>

<script lang="ts">
import AuthService from "@/service/AuthService";
import { Component, Vue } from "vue-property-decorator";
import Navigation from "./components/common/Navigation.vue";

@Component({
  components: {
    Navigation
  }
})
export default class App extends Vue {
  created() {
     AuthService.loginFromStore();
  }
}
</script>
