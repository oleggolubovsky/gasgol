<template>
  <v-content>
    <v-container fluid fill-height class="pa-0">
      <v-row class="align-center">
        <v-col>
          <v-layout align-center justify-center column>
            <v-alert
              class="text-left mb-11"
              type="success"
              v-if="isPasswordChanged"
              width="320"
            >
              You have successfully registered!
            </v-alert>
            <v-card class="mb-5 form" width="320">
              <v-toolbar color="white justify-center" flat>
                <v-toolbar-title>Create password</v-toolbar-title>
                <v-spacer></v-spacer>
                <logo size="74" />
              </v-toolbar>
              <v-form v-model="valid">
                <v-card-text>
                  <v-text-field
                    v-if="status === 'INVITED'"
                    v-model="corporation"
                    label="Company"
                    prepend-icon="mdi-domain"
                    readonly
                    required
                    :error="inputsError.corporation"
                  ></v-text-field>
                  <v-text-field
                    v-if="status === 'INVITED'"
                    v-model="firstName"
                    label="First Name"
                    prepend-icon="mdi-account"
                    maxlength="20"
                    required
                    :error="inputsError.firstName"
                    :rules="nameRules"
                  ></v-text-field>
                  <v-text-field
                    v-if="status === 'INVITED'"
                    v-model="lastName"
                    label="Last Name"
                    prepend-icon="mdi-account"
                    maxlength="20"
                    required
                    :error="inputsError.lastName"
                    :rules="nameRules"
                  ></v-text-field>
                  <v-text-field
                    v-model="email"
                    label="E-mail"
                    prepend-icon="mdi-email"
                    readonly
                    required
                    :error="inputsError.email"
                  ></v-text-field>
                  <v-text-field
                    v-model="password"
                    prepend-icon="lock"
                    name="input-10-1"
                    label="Password"
                    hint="At least 8 characters"
                    counter
                    :error="inputsError.password"
                    :rules="passwordRules"
                    :append-icon="isPasswordShown ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="isPasswordShown ? 'text' : 'password'"
                    @click:append="isPasswordShown = !isPasswordShown"
                  ></v-text-field>
                  <v-text-field
                    v-model="confirmPassword"
                    prepend-icon="lock"
                    name="input-10-1"
                    label="Confirm Password"
                    hint="At least 8 characters"
                    counter
                    :error="inputsError.confirmPassword"
                    :type="'password'"
                    :rules="[
                      v => (!!v && v === password) || 'Password doesn\'t match'
                    ]"
                  ></v-text-field>
                </v-card-text>
                <v-card-actions class="mx-2 py-4">
                  <v-spacer></v-spacer>
                  <v-btn :disabled="!valid" color="primary" @click="create">
                    Create Password
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
            src="../../assets/WAPP-login2.png"
          >
            <h1 class="display-2">WAPP.ai</h1>
            <p>
              the next generation of virtual assistant driven by AI platform to
              help customer support, lead generation and successful sales
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
import Logo from "../../components/common/Logo.vue";

@Component({
  components: {
    Logo
  }
})
export default class Login extends Vue {
  snackbar = false;
  text = "";

  isPasswordChanged = false;
  valid = false;
  password = "";
  confirmPassword = "";
  isPasswordShown = false;
  email = "";
  firstName = "";
  lastName = "";
  corporation = "Test";
  status = "";
  passwordRules = [
    (v: string) => !!v || "Password is required",
    (v: string) => v.length >= 8 || "Name must be at least 8 characters"
  ];
  nameRules = [
    (v: string) => !!v || "Name is required",
    (v: string) => v.length >= 3 || "Name must be at least 3 characters"
  ];

  inputsError: any = {
    email: false,
    password: false,
    confirmPassword: false
  };

  mounted() {
    const { token } = this.$route.params;
    if (!this.valid) {
      return;
    }
    this.$store.commit("overlay", true);
    axios
      .get("/users/confirm/" + token)
      .then(response => {
        this.email = response.data.email;
        this.firstName = response.data.firstName;
        this.lastName = response.data.lastName;
        this.status = response.data.status;
        this.corporation = response.data.corporation;
      })
      .catch(() => {
        this.snackbar = true;
        this.text = "Something vent wrong";
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }

  create() {
    const { token } = this.$route.params;
    if (!this.valid) {
      return;
    }
    this.$store.commit("overlay", true);
    axios
      .post("/users/setPassword", {
        registerLink: token,
        password: this.password,
        confirmPassword: this.confirmPassword,
        firstName: this.firstName,
        lastName: this.lastName,
        status: this.status === "FIRST" ? "FIRST" : "ACTIVE"
      })
      .then(response => {
        this.isPasswordChanged = true;
        setTimeout(() => this.$router.push({ path: "/login" }), 3000);
      })
      .catch((error: any) => {
        let err;
        let errors = [];
        if (error.response && error.response.data.errorMessage) {
          err = JSON.parse(error.response.data.errorMessage);
          if (err && err.length) {
            errors = err.map((e: any) => {
              this.inputsError[e.name] = true;
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
