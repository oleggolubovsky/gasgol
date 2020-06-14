<template>
  <v-content>
    <v-container fluid fill-height>
      <v-layout column class="px-4">
        <v-form v-model="valid" lazy-validation>
          <v-text-field
            ref="refTitle"
            class="display-1 new-list-title font-weight-medium"
            height="35"
            v-model="title"
            required
            append-icon="mdi-pencil"
            placeholder="New Contact List"
            :rules="titleRules"
            @blur="onBlur"
          ></v-text-field>
          <v-flex row>
            <v-dialog v-model="modalContacts" max-width="700px" min-height="800px">
              <template v-slot:activator="{ on }">
                <v-btn
                  class="ma-2"
                  outlined
                  color="indigo"
                  style="background-color: #fff"
                  :disabled="!title"
                  v-on="on"
                  @click="loadContactLists"
                >
                  <v-icon size="22">mdi-plus</v-icon> Add Contacts
                </v-btn>
              </template>
              <div>
                <v-card>
                  <v-card-text>
                    <div flat>
                      <v-toolbar-title class="main-title" test-left>Contact</v-toolbar-title>
                    </div>
                    <v-layout align-center justify-center>
                      <v-flex>
                        <v-text-field
                          v-model="phone"
                          label="Phone"
                          prepend-icon="mdi-phone"
                          required
                          :rules="phoneRules"
                        ></v-text-field>
                        <v-text-field
                          v-model="name"
                          label="Name"
                          prepend-icon="mdi-account"
                        ></v-text-field>
                      </v-flex>
                    </v-layout>
                    <div class="table-container">
                      <div flat>
                        <v-toolbar-title test-left>Contact Lists</v-toolbar-title>
                      </div>
                      <v-data-table
                        :headers="modalHeaders"
                        :items="contactLists"
                        class="elevation-1"
                        :items-per-page="999"
                        hide-default-footer
                        @update:sort-by="sortBy"
                        @update:sort-desc="sortDesc"
                      >
                        <template v-slot:item.enable="{ item }">
                          <v-switch v-model="item.enable"></v-switch>
                        </template>
                      </v-data-table>
                    </div>
                    <div class="py-4 buttons-container">
                      <v-btn
                        class="mx-4"
                        width="100px"
                        color="normal"
                        @click="cancel"
                        right
                      >
                        Cancel
                      </v-btn>
                      <v-btn
                        class="mx-4"
                        width="100px"
                        color="primary"
                        @click="create"
                        right
                      >
                        Save
                      </v-btn>
                    </div>
                  </v-card-text>
                </v-card>
                <v-snackbar v-model="snackbar" :multi-line="true">
                  {{ text }}
                  <v-btn color="red" text @click="snackbar = false">
                    Close
                  </v-btn>
                </v-snackbar>
              </div>
            </v-dialog>
            <v-btn class="ma-2" outlined color="indigo" style="background-color: #fff">
              <v-icon size="22">mdi-upload</v-icon> Upload Contacts: CSV, XLS
            </v-btn>
          </v-flex>
          <data-table
            :headers="headers"
            :fetch-data-params="fetchDataParams"
            :delete-item-params="deleteItemParams"
            :props-to-convert-date="propsToConvertDate"
            :dialogTitle="'Are you sure you want delete this contact?'"
          >
          </data-table>
        </v-form>
      </v-layout>
    </v-container>
  </v-content>
</template>

<style lang="scss">
.new-list-title .v-input__control > .v-input__slot {
  border: 1px solid transparent;
  border-radius: 2px;
  overflow: hidden;

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
.buttons-container {
  text-align: right;
}
.main-title {
  font-size: 2.2rem;
}
.table-container {
  .v-data-table {
    overflow: scroll;
    height: 300px;
    td {
      font-size: 1rem;
    }
  }
  .v-toolbar__title {
    font-size: 1.8rem;
    text-align: left;
  }
  .table-header-name {
    width: 50%;
  }
  .table-header-enable {
    width: 15%;
  }
  .v-data-table {
    box-shadow: none !important;
  }
}
</style>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "@/http/axios";
import {axiosWrap} from "@/http/axios";
import DataTable from "@/components/shared/DataTable.vue";

interface NewListParams {
  id?: number;
  name?: string;
}

interface ContactListParams {
  page?: number;
  size?: number;
  sort?: string;
  direction?: string;
}

interface Pagination {
  itemsLength: number;
  itemsPerPage: number;
  page: number;
  pageCount: number;
  pageStart: number;
  pageStop: number;
}

@Component({
  components: {
    DataTable
  }
})
export default class NewContactList extends Vue {
  valid = true;
  titleRules = [(v: string) => !!v || "Name is required"];
  $refs: any;

  title = "";

  modalContacts = false;

  headers = [
    { text: "Name", align: "start", value: "name" },
    { text: "Telephone Number", value: "name" },
    { text: "Creation Date", value: "createdAt" },
    { text: "Modified Date", value: "updatedAt" },
    { text: "", sortable: false, value: "edit" },
    { text: "", sortable: false, value: "delete" }
  ];

  fetchDataParams = {
    method: "get",
    url: "/contacts/all",
    withCredentials: true
  };

  deleteItemParams = {
    method: "delete",
    url: "/contacts/",
    meta: {
      param: 0 // contact list id
    },
    withCredentials: true
  };

  propsToConvertDate = ["createdAt", "updatedAt"];

  newListParams: NewListParams = {};

  snackbar = false;
  text = "";

  contactList = ['List 1'];
  name = "";
  phone = "";
  email = "";
  nameRules = [
    (v: string) => !!v || "Name is required",
    (v: string) => v.length >= 8 || "Name must be at least 8 characters"
  ];
  phoneRules = [
    (v: string) => !!v || "Phone is required",
    (v: string) => v.match(/^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{6})$/) || "Phone must be at least 12 characters"
  ];
  modalHeaders = [
    {
      text: 'Name',
      align: 'start',
      value: 'name',
      class: 'table-header-name',
    },
    { text: 'Updated date', value: 'updatedAt' },
    { text: 'Enable', value: 'enable', class: 'table-header-enable' },
  ];
  contactLists: any = [];

  fetchParams = {
    page: 0,
    size: 9999,
    sort: "updatedAt",
    direction: "desc"
  };

  mounted() {
    this.$nextTick(this.$refs.refTitle.focus);
    setTimeout(() => this.fetchData({}), 300);
  }

  loadContactLists() {
    setTimeout(() => this.fetchData({}), 300);
  }

  cancel() {
    this.modalContacts = false;
    this.contactLists = [];
  }

  create() {
    this.contactLists.forEach((item: any) => {
      this.change(item)
    })
  }

  change(item: any) {
    if(item.enable) {
      this.$store.commit("overlay", true);
      axios
        .post("/contacts", {
          "id": 0,
          "name": this.name,
          "phone": this.phone,
          "contactListId": item.id
        },{ withCredentials: true })
        .catch(() => {
          this.snackbar = true;
          this.text = "Something vent wrong";
        })
        .finally(() => {
          this.$store.commit("overlay", false);
        });
    }
  }

  onBlur() {
    if (!this.title) {
      return;
    }
    const data: NewListParams = {
      name: this.title
    };
    if (this.newListParams.id) {
      data.id = this.newListParams.id;
      this.editList(data);
    } else {
      this.createList(data);
    }
  }

  createList(data: NewListParams) {
    axios
      .post("/contactLists", data, { withCredentials: true })
      .then((res: any) => {
        this.newListParams.id = res.data.id;
      })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }

  editList(data: NewListParams) {
    axios
      .put("/contactLists", data, { withCredentials: true })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }

  fetchData(params: ContactListParams) {
    this.$store.commit("overlay", true);
    Object.assign(this.fetchParams, params);
    return axiosWrap({
      method: "get",
      url: `/contactLists`,
      params: this.fetchParams,
      withCredentials: true
    })
      .then((res: any) => {
        this.contactLists = this.getContactLists(res.content);
      })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.$store.commit("overlay", false);
      });
  }

  getDate(timestamp: number) {
    if (!timestamp) {
      return "";
    }
    const date = new Date(timestamp);
    const day = this.checkValueFormat(date.getDate());
    const month = this.checkValueFormat(date.getMonth());
    const year = this.checkValueFormat(date.getFullYear());
    const hours = this.checkValueFormat(date.getHours());
    const min = this.checkValueFormat(date.getMinutes());
    return `${day}.${month}.${year} ${hours}:${min}`;
  }

  checkValueFormat(v: number) {
    return v < 10 ? "0" + v : v;
  }

  getContactLists(list: Array<any>) {
    return list.map(item => {
      item.createdAt = this.getDate(item.createdAt);
      item.updatedAt = this.getDate(item.updatedAt);
      return item;
    });
  }

  paginate(p: Pagination) {
    this.fetchData({
      page: p.page - 1,
      size: p.itemsPerPage
    });
  }

  sortBy(name: Array<string>) {
    this.fetchData({
      sort: name[0] || "name",
      direction: "asc"
    });
  }

  sortDesc(isDesc: Array<string>) {
    this.fetchData({
      direction: isDesc[0] ? "desc" : "asc"
    });
  }
}
</script>
