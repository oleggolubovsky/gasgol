<template>
  <v-content>
    <v-container fluid fill-height class="pa-0">
      <v-row class="align-center">
        <v-col>
          <v-layout align-center justify-center column v-if="!info">
            <v-card width="450" flat class="text-left">
              <v-card-text>
                Already have an account?
                <router-link to="/login">Log in.</router-link>
              </v-card-text>
            </v-card>
            <v-card class="mb-5 form" width="450">
              <v-toolbar color="white justify-center" flat>
                <v-toolbar-title>Регистрация нового пользователя</v-toolbar-title>
                <v-spacer></v-spacer>
                <logo size="74" />
              </v-toolbar>
              <v-form v-model="valid">
                <v-card-text>
                  <v-text-field
                    v-model="firstName"
                    label="First Name"
                    prepend-icon="mdi-account"
                    maxlength="20"
                    required
                    :error="inputsError.firstName"
                    :rules="nameRules"
                  ></v-text-field>
                  <v-text-field
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
                    required
                    :error="inputsError.email"
                    :error-messages="inputsError.email ? 'E-mail is already in use' : ''"
                    :rules="emailRules"
                    @blur="checkEmail"
                    @input="inputsError.email = false"
                  ></v-text-field>
                </v-card-text>
                <v-card-actions class="mx-2">
                  <v-spacer></v-spacer>
                  <v-btn :disabled="!valid" color="primary" @click="sendData"
                    >Register</v-btn
                  >
                </v-card-actions>
                <v-card-actions class="mx-2">
                  <v-checkbox
                    v-model="checkbox"
                    :rules="[v => !!v || 'You must agree to continue!']"
                    required
                  >
                    <div class="caption" slot="label">
                      By registering you agree to our
                      <router-link to="/terms-of-service">Terms of Service</router-link>,
                      <router-link to="/privacy-policy">Privacy Policy</router-link>
                      and our
                      <router-link to="/acceptable-use-policy">Acceptable Use Policy</router-link>
                    </div>
                  </v-checkbox>
                </v-card-actions>
              </v-form>
            </v-card>
          </v-layout>
          <registration-info
            v-if="info"
            :email="email"
            @resend-email="beckToRegister"
          ></registration-info>
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
import axios from "../../http/axios";
import RegistrationInfo from "../../components/registration/RegistrationInfo.vue";
import Logo from "../../components/common/Logo.vue";

@Component({
  components: {
    RegistrationInfo,
    Logo
  }
})
export default class Registration extends Vue {
  info: any = null;

  checkbox = false;
  valid = false;
  firstName = "";
  lastName = "";
  email = "";
  nameRules = [
    (v: string) => !!v || "Name is required",
    (v: string) => v.length >= 3 || "Name must be at least 3 characters"
  ];
  emailRules = [
    (v: string) => !!v || "E-mail is required",
    (v: string) => /.+@.+/.test(v) || "E-mail must be valid"
  ];
  inputsError: any = {
    firstName: false,
    lastName: false,
    email: false
  };

  sendData() {
    if (!this.valid) {
      return;
    }
    this.$store.commit("overlay", true);
    axios
      .post("/users/register", {
        email: this.email,
        firstName: this.firstName,
        lastName: this.lastName,
        status: "FIRST"
      })
      .then(response => (this.info = response.data))
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

  checkEmail() {
    setTimeout(() => {
      this.$store.commit("overlay", true);
      axios
        .post("/users/email", {
          email: this.email
        })
        .then(response => (this.inputsError.email = response.data))
        .catch(() => {
          this.$store.commit("snackbar", "Sorry email can't be checked");
        })
        .finally(() => {
          this.$store.commit("overlay", false);
        });
    }, 200);
  }

  beckToRegister() {
    this.info = null;
  }
}
</script>
