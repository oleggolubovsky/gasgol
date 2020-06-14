<template>
  <v-content class="list-container">
    <v-container fluid fill-height class="align-stretch flex-column">
      <div>
        <v-toolbar-title class="ml-4 text-left"
          >Message Templates</v-toolbar-title
        >
      </div>
      <v-breadcrumbs :items="breadcrumbsList" class="pl-4 pt-2">
        <template v-slot:divider>
          <v-icon>mdi-chevron-right</v-icon>
        </template>
      </v-breadcrumbs>
      <v-card flat tile class="d-flex justify-space-between">
        <div class="justify-start">
          <v-btn color="primary" class="ma-2 new-list-btn main-btn">
            <v-icon size="22">mdi-plus</v-icon> New Template
          </v-btn>
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
import axios from "@/http/axios";
import Dialog from "../../components/shared/Dialog.vue";
import DataTable from "./DataTable.vue";

@Component({
  components: {
    DataTable,
    Dialog,
  },
})
export default class List extends Vue {
  headers = [
    { text: "", align: "start", sortable: false, value: "icon", width: 50 },
    { text: "Name", align: "start", value: "name", width: "50%" },
    { text: "Creation Date", value: "createdAt" },
    { text: "Status", value: "status" },
    { text: "", align: "end", sortable: false, value: "edit", width: 40 },
    { text: "", align: "end", sortable: false, value: "delete", width: 40 },
  ];

  fetchDataParams = {
    method: "get",
    url: "/templates",
    params: {
      templateId: undefined,
    },
    withCredentials: true,
  };

  deleteItemParams = {
    method: "delete",
    url: "/templates/",
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
      text: "Message Templates",
      disabled: true,
      href: "/message-templates",
    },
  ];

  selectItems = [{ name: "", id: 0 }] as any[];
  result = { name: "" };
  search = null;
  isLoading = false;

  autocompleteSelected(result: any) {
    if (!result) return;

    if (result.id != 0) {
      this.fetchDataParams.params.templateId = result.id;
    } else {
      this.fetchDataParams.params.templateId = undefined;
    }
  }

  @Watch("search", { immediate: true })
  selectSearch(search: any) {
    if (!search || this.result.name == search) return;

    this.isLoading = true;

    axios
      .get("/templates/search", { params: { search, limit: 10 } })
      .then((response) => {
        this.selectItems = [{ name: "", id: 0 }, ...response.data];
      })
      .finally(() => {
        this.isLoading = false;
      });
  }
}
</script>
