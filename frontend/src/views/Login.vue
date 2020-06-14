<template>
  <v-content>
    <v-container fluid fill-height class="pa-0">
      <v-row class="align-center">
        <v-col>
          <v-layout align-center justify-center column>
            <v-card width="420" flat class="text-left">
              <v-card-text>
                У вас уже есть аккаунт?
                <router-link to="/register">Регистрация.</router-link>
              </v-card-text>
            </v-card>
            <v-card class="mb-5 form" width="420">
              <v-toolbar color="white justify-center" flat>
                <v-toolbar-title>Вход</v-toolbar-title>
                <v-spacer></v-spacer>
                <logo size="74" />
              </v-toolbar>
              <v-form v-model="valid">
                <v-card-text class="justify-center">
                  <v-text-field
                    v-model="email"
                    label="E-mail"
                    prepend-icon="mdi-email"
                    required
                    :error="inputsError"
                    :rules="emailRules"
                    @input="inputsError = false"
                  ></v-text-field>
                  <v-text-field
                    v-model="password"
                    prepend-icon="lock"
                    name="input-10-1"
                    label="Пароль"
                    :error="inputsError"
                    :append-icon="isPasswordShown ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="isPasswordShown ? 'text' : 'password'"
                    @click:append="isPasswordShown = !isPasswordShown"
                    @input="inputsError = false"
                  ></v-text-field>
                </v-card-text>
                <div class="error" v-if="inputsError">{{ text }}</div>
                <v-card-actions class="pa-4 justify-center">
                  <router-link to>Забыли пароль?</router-link>
                  <v-spacer></v-spacer>
                  <v-btn :disabled="!valid" color="primary" @click="validate">
                    Вход
                  </v-btn>
                </v-card-actions>
              </v-form>
            </v-card>
          </v-layout>
        </v-col>
        <v-col class="pa-0">
          <v-img
            :aspect-ratio="1 / 2"
            height="100vh"
            class="text-left pa-4"
            src="../assets/WAPP-login2.png"
          >
            <h1 class="display-2">каблук.бел</h1>
                        <p>
                          начни продавать и покупать с Каспером
                        </p>
          </v-img>
        </v-col>
      </v-row>
    </v-container>
  </v-content>
</template>

<style scoped>
.v-card.form {
  box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.3);
}
.logo {
  margin-top: -60px;
  background-color: #fff;
}
</style>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "@/http/axios";
import AuthService from "@/service/AuthService";
import Logo from "../components/common/Logo.vue";

@Component({
  components: {
    Logo,
  },
})
export default class Login extends Vue {
  snackbar = false;
  text = "email or password is not valid";

  valid = false;
  isPasswordShown = false;
  email = "";
  password = "";
  loginData = {};
  emailRules = [
    (v: string) => !!v || "E-mail is required",
    (v: string) => /.+@.+/.test(v) || "E-mail must be valid",
  ];

  inputsError = false;

  validate() {
    if (!this.valid) {
      return;
    }
    this.$store.commit("overlay", true);
    axios
      .post("/users/login", { email: this.email, password: this.password })
      .then((response) => {
        if (response.data.status === "ACTIVE") {
          AuthService.login(response.data);
          this.$store.commit("login");
          this.$router.push({ path: "/" });
        } else {
          AuthService.login(response.data);
          this.$store.commit("login", response.data);
          this.$router.push({ path: "/user-list/first" });
        }
      })
      .catch((error: any) => {
        let err;
        let errors = [];
        if (error.response && error.response.data.errorMessage) {
          err = JSON.parse(error.response.data.errorMessage);
          if (err && err.length) {
            errors = err.map((e: any) => {
              this.inputsError = true;
              return e.error;
            });
          }
        }
        this.$store.commit(
          "snackbar",
          `Something vent wrong. ${errors.join(". ")}`
        );
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }
}
</script>
