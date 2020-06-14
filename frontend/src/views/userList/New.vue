<template>
  <v-content>
    <v-container fluid fill-height>
      <v-layout column>
        <v-toolbar-title class="ml-4 text-left">User Information</v-toolbar-title>
        <v-row>
          <v-col class="pl-6">
            <v-form class="text-left" v-model="valid" lazy-validation>
              <v-text-field
                v-model="corporation"
                :rules="nameRules"
                label="Corporation"
                prepend-icon="mdi-folder"
              ></v-text-field>
              <v-select
                v-model="role"
                :items="['ADMIN', 'OPERATOR']"
                label="Role"
                prepend-icon="mdi-account-group"
                :readonly="this.isFirst"
              ></v-select>
              <v-text-field
                v-model="firstName"
                :rules="nameRules"
                label="First Name"
                prepend-icon="mdi-account"
              ></v-text-field>
              <v-text-field
                v-model="lastName"
                :rules="nameRules"
                label="Last Name"
                prepend-icon="mdi-account"
              ></v-text-field>
              <v-text-field
                v-model="email"
                label="Login"
                prepend-icon="mdi-email"
                readonly=""
              ></v-text-field>
              <div class="my-4">
                <router-link to>Change Password</router-link>
              </div>
              <div class="pt-4 buttons-container text-right">
                <v-btn
                  class="secondary-btn mx-4 main-btn"
                  width="100px"
                  color="normal"
                  @click="cancel"
                  right
                >
                  Cancel
                </v-btn>
                <v-btn
                  class="ml-4 main-btn"
                  width="100px"
                  color="primary"
                  @click="create"
                  right
                  :disabled="!valid"
                >
                  Save
                </v-btn>
              </div>
            </v-form>
          </v-col>
          <v-col></v-col>
        </v-row>
      </v-layout>
    </v-container>
  </v-content>
</template>

<style lang="scss">

</style>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "@/http/axios";
import DataTable from "@/components/shared/DataTable.vue";

@Component({
  components: {
    DataTable
  }
})
export default class NewUser extends Vue {
  valid = false;
  email = "";
  id = null;
  firstName = "";
  lastName = "";
  corporation = "";
  status = "";
  role = "ADMIN";
  isFirst = false;
  nameRules = [
    (v: string) => !!v || "Field is required",
    (v: string) => v.length >= 3 || "Field must be at least 3 characters"
  ];

  mounted() {
    const { id } = this.$route.params;
    if (!this.valid) {
      return;
    }
    this.$store.commit("overlay", true);
    switch (id) {
      case "first": {
        this.isFirst = true;
        const userData = JSON.parse(localStorage.getItem("userData") || "");
        this.email = userData.email;
        this.firstName = userData.firstName;
        this.lastName = userData.lastName;
        this.status = userData.status;
        this.id = userData.id;
        this.$store.commit("overlay", false);
        break;
      }
      default: {
        axios
          .get("/users/" + id)
          .then(response => {
            this.email = response.data.email;
            this.firstName = response.data.firstName;
            this.lastName = response.data.lastName;
            this.status = response.data.status;
            this.id = response.data.id;
            this.corporation = response.data.corporation;
          })
          .finally(() => {
            this.$store.commit("overlay", false);
          });
      }
    }
  }

  create() {
    const { token } = this.$route.params;
    if (!this.valid) {
      return;
    }
    this.$store.commit("overlay", true);
    axios
      .put("/users", {
        email: this.email,
        firstName: this.firstName,
        lastName: this.lastName,
        corporation: this.corporation,
        id: this.id,
        role: this.role,
        status: "ACTIVE"
      })
      .then(response => {
        if (this.isFirst) {
          this.$store.commit("logout");
          this.$router.push({ path: "/login" });
        } else {
          this.$router.push({ path: "/user-list" });
        }
      })
      .catch((error: any) => {
        let err;
        let errors = [];
        if (error.response && error.response.data.errorMessage) {
          err = JSON.parse(error.response.data.errorMessage);
          if (err && err.length) {
            errors = err.map((e: any) => {
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
  cancel() {
    if (this.isFirst) {
      this.$store.commit("logout");
      this.$router.push({ path: "/login" });
    } else {
      this.$router.push({ path: "/user-list" });
    }
  }
}
</script>
