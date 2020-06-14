<template>
  <div>
    <v-card>
      <v-card-text>
        <div flat>
          <v-toolbar-title test-left>Contact</v-toolbar-title>
        </div>
        <v-layout align-center justify-center>
          <v-flex>
            <v-text-field
              v-model="phone"
              label="Phone"
              prepend-icon="mdi-phone"
              required
              :rules="phoneRules"
              type="number"
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
            <v-toolbar-title class="pl-3" test-left>Contact Lists</v-toolbar-title>
          </div>
          <v-data-table
            :headers="modalHeaders"
            :items="contactLists"
            class="elevation-1 table-header"
            :items-per-page="999"
            hide-default-footer
            @update:sort-by="sortBy"
            @update:sort-desc="sortDesc"
          >
            <template v-slot:item.enable="{ item }">
              <v-switch v-model="item.enable"></v-switch>
            </template>
          </v-data-table>
          <div class="scroll-wrapper">
            <v-data-table
              :headers="modalHeaders"
              :items="contactLists1"
              class="elevation-1 table-body"
              :items-per-page="999"
              hide-default-footer
              @update:sort-by="sortBy"
              @update:sort-desc="sortDesc"
            >
              <template v-slot:item.enable="{ item }">
                <v-switch v-model="item.enable"></v-switch>
              </template>
            </v-data-table>
            <v-data-table
              :headers="modalHeaders"
              :items="contactLists"
              class="elevation-1 table-body"
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
        </div>
        <div class="pt-4 buttons-container">
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
            class="mx-4 main-btn"
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
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import axios from "@/http/axios";
import {axiosWrap} from "@/http/axios";
import VueTelInput from "vue-tel-input";

interface ContactListParams {
  page?: number;
  size?: number;
  sort?: string;
  direction?: string;
  phoneNumber?: any;
}

interface NewListParams {
  id?: number;
  name?: string;
}

@Component({
  components: {
    VueTelInput
  }
})
@Component
export default class AddNewUser extends Vue {
  @Prop()
  newListParams!: any;
  @Prop()
  initialPhone!: any;

  snackbar = false;
  text = "";

  contactList = ['List 1'];
  valid = false;
  name = "";
  phone = "";
  email = "";
  nameRules = [
    (v: string) => !!v || "Name is required",
    (v: string) => v.length >= 8 || "Name must be at least 8 characters"
  ];
  phoneRules = [
    (v: string) => !!v || "Phone is required",
    (v: string) => !!v.match(/^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{6})$/) || "Phone must be at least 12 characters"
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
  contactLists1: any = [];

  fetchParams = {
    page: 0,
    size: 9999,
    sort: "updatedAt",
    direction: "desc"
  };

  mounted() {
    this.phone = window.initialPhone;
    setTimeout(() => this.fetchData({ phoneNumber: window.initialPhone || '1' }), 300);
  }

  cancel() {
    this.contactLists = [];
    this.contactLists1 = [];
    this.$emit("close-contact-model");
  }

  create() {
    const contactListIds: Array<any> = [];
    this.contactLists.forEach((item: any) => {
      if (item.enable) {
        contactListIds.push(item.id);
      }
    });
    this.change(contactListIds)
  }

  change(item: any) {
    if(item) {
      this.$store.commit("overlay", true);
      axios
        .post("/contacts", {
          "contactListId": window.newListParams.id,
          "name": this.name,
          "phone": this.phone,
          "contactListIds": item
        },{ withCredentials: true })
        .then(res => {
          this.contactLists = [];
          this.contactLists1 = [];
          this.$emit("close-contact-model");
        })
        .catch(() => {
          this.snackbar = true;
          this.text = "Something vent wrong";
        })
        .finally(() => {
          this.$store.commit("overlay", false);
        });
    }
  }

  

  fetchData(params: ContactListParams) {
    Object.assign(this.fetchParams, params);
    return axiosWrap({
      method: "get",
      url: `/contactLists/phone`,
      params: this.fetchParams,
      withCredentials: true
    })
      .then((res: any) => {
        this.contactLists = this.getContactLists(res);
        const enabled = this.contactLists.find((item: any) => item.id === window.newListParams.id);
        this.contactLists1 = [enabled];
        enabled.enable = true;
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
    return `${day}.${month}.${year}`;
  }

  checkValueFormat(v: number) {
    return v < 10 ? "0" + v : v;
  }

  getContactLists(list: Array<any>) {
    return list.map(item => {
      item.createdAt = this.getDate(item.createdAt);
      item.updatedAt = this.getDate(item.updatedAt);
      return item;
    })
    .filter(item => item.id);
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
Vue.use(VueTelInput)
</script>
<style lang="scss">
.buttons-container {
  text-align: right;
}
.main-title {
  font-size: 2.2rem;
}
.table-container {
  .table-header {
    height: 50px;
    overflow: hidden;
  }
  .table-body {
    .v-data-table-header {
      display: none;
    }
  }
  .v-data-table {
    td {
      font-size: 1rem;
    }
  }
  .v-toolbar__title {
    text-align: left;
  }
  .table-header-name {
    width: 50%;
  }
  .table-header-enable {
    width: 20%;
  }
  .v-data-table {
    box-shadow: none !important;
    td {
      font-size: 0.875rem;
    }
    tr {
      td:first-child {
        width: 50%;
      }
      td:last-child {
        width: 20%;
      }
    }
  }
}
.scroll-wrapper {
  height: 200px;
  overflow: auto;
}
</style>
