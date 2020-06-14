<template>
  <v-content class="list-container">
    <v-container fluid fill-height class="align-stretch flex-column">
      <div><v-toolbar-title class="ml-4 text-left">Users</v-toolbar-title></div>
      <v-breadcrumbs :items="breadcrumbsList" class="pl-4 pt-2">
        <template v-slot:divider>
          <v-icon>mdi-chevron-right</v-icon>
        </template>
      </v-breadcrumbs>
      <v-card flat tile class="d-flex justify-space-between">
        <div class="justify-start">
          <v-dialog
            v-model="modalContacts"
            max-width="500px"
            min-height="800px"
          >
            <template v-slot:activator="{ on }">
              <v-btn
                color="primary"
                v-on="on"
                class="ma-2 new-list-btn main-btn pr-5"
              >
                <v-icon size="22">mdi-plus</v-icon> New User
              </v-btn>
            </template>
            <v-card>
              <v-container>
                <v-form v-model="validInvite">
                  <v-text-field
                    v-model="email"
                    label="E-mail"
                    prepend-icon="mdi-email"
                    required
                    :error="emailError"
                    :error-messages="
                      emailError ? 'E-mail is already in use' : ''
                    "
                    :rules="emailRules"
                    @blur="checkEmail"
                    @input="emailError = false"
                  ></v-text-field>
                </v-form>
                <v-btn :disabled="!validInvite" color="primary" @click="sendData"
                  >Send email</v-btn
                >
              </v-container>
            </v-card>
          </v-dialog>
        </div>
        <div class="justify-end" style="width: 400px;">
          <v-autocomplete
            :prepend-icon="'mdi-magnify'"
            :items="selectItems"
            :loading="isLoading"
            @change="autocompleteSelected"
            v-model="result"
            item-text="name"
            :search-input.sync="search"
            return-object
            label="Search"
            dense
            outlined
          ></v-autocomplete>
        </div>
      </v-card>
      <data-table
        :headers="headers"
        :fetch-data-params="fetchDataParams"
        :delete-item-params="deleteItemParams"
        :props-to-convert-date="propsToConvertDate"
        :dialogTitle="'Are you sure you want delete this contact list?'"
      >
      </data-table>
    </v-container>
  </v-content>
</template>

<style lang="scss">
.list-container {
  .new-list-btn {
    .v-btn__content {
      i {
        margin-right: 5px;
      }
    }
  }
  table {
    td {
      padding-right: 0;
    }
  }
}
</style>
<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import Dialog from "../../components/shared/Dialog.vue";
import DataTable from "./DataTable.vue";
import axios from "@/http/axios";

@Component({
  components: {
    DataTable,
    Dialog,
  },
})
export default class List extends Vue {
  headers = [
    { text: "", align: "start", sortable: false, value: "icon", width: 50 },
    { text: "First Name", align: "start", value: "firstName", width: "25%" },
    { text: "Last Name", align: "start", value: "lastName", width: "25%" },
    { text: "Email", value: "email" },
    { text: "Role", value: "role" },
    { text: "", align: "end", sortable: false, value: "edit", width: 40 },
    { text: "", align: "end", sortable: false, value: "delete", width: 40 },
  ];

  fetchDataParams = {
    method: "get",
    url: "/users/all",
    withCredentials: true,
    params:{
      userId: undefined
    }
  };

  deleteItemParams = {
    method: "delete",
    url: "/users/all/",
    withCredentials: true,
  };

  propsToConvertDate = ["createdAt", "updatedAt"];

  breadcrumbsList = [
    {
      text: "WAPP",
      disabled: false,
      href: "/",
    },
    {
      text: "Users",
      disabled: true,
      href: "/users-list",
    },
  ];

  validInvite = false;
  email = "";
  emailRules = [
    (v: string) => !!v || "E-mail is required",
    (v: string) => /.+@.+/.test(v) || "E-mail must be valid",
  ];
  emailError = false;

  modalContacts = false;

  sendData() {
    this.$store.commit("overlay", true);
    axios
      .post("/users/invite", {
        email: this.email,
        status: "INVITED",
      })
      .then((response) => {
        this.modalContacts = false;
      })
      .catch((error: any) => {
        this.emailError = true;
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

  selectItems = [{ name: "", id: 0 }] as any[];
  result = { name: "" };
  search = null;
  isLoading = false;

  autocompleteSelected(result: any) {
    if (!result) return;

    if (result.id != 0) {
      this.fetchDataParams.params.userId = result.id;
    } else {
      this.fetchDataParams.params.userId = undefined;
    }
  }

  @Watch("search", { immediate: true })
  selectSearch(search: any) {
    if (!search || this.result.name == search) return;

    this.isLoading = true;

    axios
      .get("/users/search", { params: { search, limit: 10 } })
      .then((response) => {
        this.selectItems = [{ name: "", id: 0 }, ...response.data];
      })
      .finally(() => {
        this.isLoading = false;
      });
  }
}
</script>
