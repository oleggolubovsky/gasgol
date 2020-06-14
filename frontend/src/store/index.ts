import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    overlay: false,
    menuDisabled: !!localStorage.getItem('menuDisabled'),
    userData: {},
    snackbar: {
      isVisible: false,
      text: ""
    },
    user: {
      authenticated: !!localStorage.getItem('login')
    }
  },
  mutations: {
    overlay(state, isInProgress) {
      state.overlay = isInProgress;
    },
    snackbar(state, message) {
      state.snackbar.isVisible = !!message;
      state.snackbar.text = message;
    },
    login(state, data) {
      state.menuDisabled = false;
      localStorage.setItem('login', 'true');
      state.user.authenticated = true;
      localStorage.setItem('menuDisabled', '');
      if (data) {
        state.menuDisabled = true;
        localStorage.setItem('menuDisabled', 'true');
        localStorage.setItem('userData', JSON.stringify(data));
        state.userData = data;
      }
    },
    logout(state) {
      localStorage.removeItem('login');
      state.user.authenticated = false;
    }
  },
  actions: {},
  modules: {}
});
