<template>
  <v-content class="add-contact">
    <v-row>
      <v-col class="pt-0">
        <v-container fluid fill-height>
          <v-layout column class="px-4">
            <v-form v-model="valid" lazy-validation>
              <v-text-field
                class="main-input"
                ref="refTitle"
                height="35"
                v-model="title"
                required
                :label="titlePlaceholder"
                :rules="titleRules"
                @blur="onBlur"
              ></v-text-field>
              <v-breadcrumbs :items="breadcrumbsList" class="pl-0 pt-0">
                <template v-slot:divider>
                  <v-icon>mdi-chevron-right</v-icon>
                </template>
              </v-breadcrumbs>
              <v-flex row>
                <v-dialog v-model="modalContacts" max-width="500px" min-height="800px">
                  <template v-slot:activator="{ on }">
                    <v-btn
                      class="ma-2 main-btn"
                      color="primary"
                      :disabled="!title"
                      v-on="on"
                    >
                      <v-icon size="22">mdi-plus</v-icon> Add Contacts
                    </v-btn>
                  </template>
                  <add-new-user
                    v-if="modalContacts"
                    :newListParams="newListParams.id"
                    :initialPhone="initialPhone"
                    @close-contact-model="closeModalContacts"
                  ></add-new-user>
                </v-dialog>
                <v-btn color="primary" @click="uploadListShow" :disabled="!title" class="ma-2 main-btn">
                  <v-icon size="22">mdi-upload</v-icon> Upload Contacts: CSV, XLS
                </v-btn>
              </v-flex>
              <data-table
                v-if="reloadData"
                :headers="headers"
                :fetch-data-params="$route.params.id === 'new' ? false : fetchDataParams"
                :delete-item-params="deleteItemParams"
                :props-to-convert-date="propsToConvertDate"
                :dialogTitle="'Are you sure you want delete this contact?'"
                @changePhone="changePhone"
              >
              </data-table>
            </v-form>
          </v-layout>
        </v-container>

        <v-dialog v-model="uploadListDialogShow" @close="close" max-width="500px">
          <v-form v-if="uploadListDialogShow" ref="uploadListForm" v-model="uploadListFormValid" lazy-validation>
            <v-card class="px-6 py-6">
              <p class="headline text-left text--darken-1">
                <span>Upload Contacts List</span>
              </p>

              <div class="pt-6 text-left">
                <v-file-input
                  class="file-input"
                  ref="uploadListDialogFile"
                  accept=".xls,.xlsx,.csv"
                  label="Select files containing contacts"
                  outlined
                  dense
                  @change="uploadListDialogChange"
                  ></v-file-input>
                <p class="mt-0 pt-0 pl-11 grey--text">supported formats: csv, xls</p>
                <v-toolbar-title
                  v-if="uploadFileError"
                  opacity="1"
                  class="mt-0 pt-0 red--text text-center headline"
                  style="white-space: normal;"
                  >Error: {{ uploadFileError }}
                </v-toolbar-title>
                <v-checkbox
                  v-model="updateAll"
                  class="pl-0 black--text"
                  label="Update All Contacts"
                ></v-checkbox>
              </div>

              <v-card-actions class="mt-8 pa-0">
                <v-spacer></v-spacer>
                <v-btn
                  class="secondary-btn mx-4 main-btn"
                  width="100px"
                  color="normal"
                  @click="close"
                  >
                  Close
                </v-btn>
                <v-btn
                  class="ml-4 main-btn"
                  width="100px"
                  color="primary"
                  :loading="uploadListDialogLoading"
                  @click="uploadListSave"
                  :disabled="!!uploadFileError"
                  >
                  Upload
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-form>
        </v-dialog>
      </v-col>
    </v-row>
  </v-content>
</template>

<style lang="scss">
.add-contact {
  .v-toolbar__title {
    font-weight: bold;
  }
  .main-input {
    width: 40%;
    .v-text-field__details {
      display: none;
    }
  }
  .new-list-title .v-input__control > .v-input__slot {
    

    &:hover {
      border-color: #ccc;

      .v-input__append-inner {
        opacity: 1;
      }
    }

    &:before,
    &:after {
      content: none;
    }

    .v-input__append-inner {
      opacity: 0;
      padding: 4px 4px 6px;
      margin-top: 0;
      background-color: #ccc;
    }
  }
  .new-list-title.v-input--is-focused .v-input__control > .v-input__slot {
    border-color: #ccc;

    .v-input__append-inner {
      opacity: 1;
    }
  }
  .theme--light.v-text-field > .v-input__control > .v-input__slot:before {
    border-color: #fff;
  }
  .v-text-field__slot input {
    font-weight: bold;
    font-size: 20px;
  }
  .main-btn {
    i {
      margin-right: 5px;
    }
  }
  table {
    td {
      padding-right: 0;
    }
  }
}
.file-input {
  .v-text-field__details {
    display: none;
  }
}
</style>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios, { axiosWrap } from "@/http/axios";
import DataTable from "@/components/shared/DataTableView.vue";
import AddNewUser from "./AddNewUser.vue";

interface NewListParams {
  id?: number;
  name?: string;
}

declare global {
  interface Window {
    newListParams: any;
  }
}

declare global {
  interface Window {
    initialPhone: any;
  }
}

@Component({
  components: {
    DataTable,
    AddNewUser
  }
})
export default class NewContactList extends Vue {
  valid = true;
  titleRules = [(v: string) => !!v || ""];
  $refs: any;

  title = "";
  titlePlaceholder = "";
  isTitleDisabled = false;
  isNew = true
  initialPhone = "";

  headers = [
    { text: "Name", align: "start", value: "name", width: "50%" },
    { text: "Telephone Number", align: "start", value: "phone" },
    { text: "Creation Date", align: "start", value: "createdAt" },
    { text: "Modified Date", align: "start", value: "updatedAt" },
    { text: "", sortable: false, align: "end", value: "edit", width: 40 },
    { text: "", sortable: false, align: "end", value: "delete", width: 40 }
  ];

  fetchDataParams: any = null;
  deleteItemParams: any = null;

  propsToConvertDate = ["createdAt", "updatedAt"];

  newListParams: NewListParams = {};

  modalContacts = false;
  breadcrumbsList = [
    {
      text: 'WAPP',
      disabled: false,
      href: '/',
    },
    {
      text: 'Contacts Lists',
      disabled: false,
      href: '/contact-list',
    },
    {
      text: 'New',
      disabled: true,
      href: '/contact-list/new',
    },
  ];
  
  uploadFileError: any = "";
  
  updateAll = true;
  reloadData = true;

  mounted() {
    this.fetchData();
  }

  fetchData() {
    window.newListParams = this.newListParams;

    this.fetchDataParams = {
      method: "get",
      url: "/contacts/all",
      params: {
        contactListId: this.$route.params.id
      },
      withCredentials: true
    };

    this.deleteItemParams = {
      method: "delete",
      url: "/contacts/",
      meta: {
        param: 0 // contact list id
        // param1: parseInt(this.$route.params.id)
      },
      withCredentials: true
    };

    const id = this.$route.params.id
    if(id == 'new') {
      this.titlePlaceholder = 'Contact List Name'
      this.$nextTick(this.$refs.refTitle.focus);
    } else {
      this.titlePlaceholder = 'Contact List Name'
      axios
        .get(`/contactLists/${id}`, { withCredentials: true })
        .then((res: any) => {
          if (res.data.name) {
            this.newListParams.id = res.data.id
            this.title = res.data.name.trim();
            this.isNew = false
          }
        })
        .catch(() => {
          this.$store.commit("snackbar", "Something vent wrong");
        })
        .finally(() => {
          this.$store.commit("overlay", false);
        });
    }

    this.fetchDataParams = {
      method: "get",
      url: "/contacts/all",
      params: {
        contactListId: this.$route.params.id
      },
      withCredentials: true
    };

    this.deleteItemParams = {
      method: "delete",
      url: "/contacts/",
      meta: {
        // param: 0 // contact list id
        param1: parseInt(this.$route.params.id)
      },
      withCredentials: true
    };
  }

  onBlur() {
    if (!this.title) {
      return;
    }
    this.newListParams.name = this.title

    if (!this.isNew) {
      this.updateList(this.newListParams);
    } else {
      this.createList(this.newListParams);
    }
  }

  createList(data: NewListParams) {
    axios
      .post("/contactLists", data, { withCredentials: true })
      .then((res: any) => {
        this.newListParams.id = res.data.id;
        this.isNew = false
      })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }

  updateList(data: NewListParams) {
    axios
      .put("/contactLists", data, { withCredentials: true })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }

  uploadListDialogShow = false
  uploadListDialogLoading = false
  uploadListDialogFilaData = ''
  uploadListRules = [(v: string) => !!v || "Строчка с ошибкой от Олега"];
  uploadListFormValid = false
  
  uploadListShow() {
    this.uploadListDialogShow = !this.uploadListDialogShow
  }

  uploadListDialogChange(file: any) {
    this.uploadListDialogFilaData = file;
    const type = file.name.split(".").pop();
    if (type === "xls" || type === "xlsx" || type === "csv") {
      this.uploadFileError = null;
    } else {
      this.uploadFileError = "Type file error";
    }
  }
  
  async uploadListSave() {
    if (!this.$refs.uploadListForm.validate()) {
        return false;
    }
    
    if(!this.uploadListDialogFilaData) {
      console.log('list upload fail: no file selected')
      return
    }

    this.uploadListDialogLoading = true;

    const formData = new FormData();
    formData.append('file', this.uploadListDialogFilaData);

    return axiosWrap({
      method: "post",
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      url: "/contacts/upload",
      data: formData,
      withCredentials: true
    })
    .then((res: any) => {
      if(!res) {
        return
      }

      // send uploaded file for processings
      axiosWrap({
        url: "/contacts/regUploadFile",
        method: 'post',
        params: {
          contactListId: this.newListParams.id,
          updateAll: this.updateAll,
          link: res,
        },
        withCredentials: true })
        .then((res: any) => {
          this.uploadListDialogShow = false
          this.fetchData();
          this.reloadData = false;
          setTimeout(() => {this.reloadData = true;}, 300);
          // TODO
          // api method isnt working yet
        })
        .catch((error: any) => {
          this.uploadFileError = error.response.data.errorMessage;
          this.$store.commit("snackbar", "Something vent wrong");
        })
        .finally(() => {
          this.$store.commit("overlay", false);
        });
    })
    .catch(() => {
      this.$store.commit("snackbar", "Something vent wrong");
    })
    .finally(() => {
      this.uploadListDialogLoading = false;
      this.$store.commit("overlay", false);
    });
  }

  cancel() {
    this.modalContacts = false;
  }
  changePhone(phone: any) {
    this.modalContacts = true;
    this.initialPhone = phone;
    window.initialPhone = phone;
  }
  close() {
    this.uploadListDialogShow = false;
    this.uploadFileError = false;
  }
  closeModalContacts() {
    this.modalContacts = false;
    this.fetchData();
    this.reloadData = false;
    setTimeout(() => {this.reloadData = true;}, 300);
  }
}
</script>
