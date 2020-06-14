<template>
  <div class="data-table">
    <v-data-table
      v-if="fetchDataParams"
      class="flex-grow-1"
      loading-text="Loading... Please wait"
      :style="{ 'background-color': bgColor }"
      :headers="headers"
      :items="lists"
      :items-per-page="20"
      :footer-props="{ 'items-per-page-options': [10, 20, 50, 100] }"
      :loading="loading"
      :server-items-length="itemsLength"
      @pagination="paginate"
      @update:sort-by="sortBy"
      @update:sort-desc="sortDesc"
    >
      <template v-slot:item.icon>
        <v-icon color="primary">mdi-format-list-bulleted-square</v-icon>
      </template>
      <template v-slot:item.name="{ item }">
        <v-btn
          style="min-width: 0"
          class="custom-transform-class text-none"
          text
           @click="$emit('changePhone', item.phone)"
        >
          {{ item.name }}
        </v-btn>
      </template>
      <template v-slot:item.edit="{ item }">
        <v-btn color="primary" fab x-small outlined @click="$emit('changePhone', item.phone)">
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
      </template>
      <template v-slot:item.delete="{ item }">
        <v-btn color="error" fab x-small outlined @click="onDialogOpen(item.id || 0)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
      <slot></slot>
    </v-data-table>
    <Dialog
      :is-shown="isDialogShown"
      :title="dialogTitle"
      @onClose="onDialogClose"
    />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from "vue-property-decorator";
import {axiosWrap} from "@/http/axios";
import Dialog from "../../components/shared/Dialog.vue";

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
    Dialog
  }
})
export default class DataTable extends Vue {
  @Prop({ default: "#fff" })
  bgColor!: string;
  @Prop({ default: () => [] })
  headers!: Array<any>;
  @Prop({ default: () => [] })
  propsToConvertDate!: Array<any>;
  @Prop({ default: "" })
  dialogTitle!: string;
  @Prop()
  fetchDataParams!: any;
  @Prop()
  deleteItemParams!: any;

  @Watch('fetchDataParams.params.contactId')
  changeFetchDataParams(fetchDataParams: any) {
    this.fetchData({});
  }

  lists: any = [];
  fetchParams = {
    page: 0,
    size: 19,
    sort: "name",
    direction: "asc"
  };
  itemsLength = 0;
  loading = true;
  isDialogShown = false;
  itemToRemove?: number | null;
  paginationMemorize = { page: 0, itemsPerPage: 19 };

  fetchData(params: ContactListParams) {
    this.$store.commit("overlay", true);
    this.loading = true;
    Object.assign(this.fetchParams, params);
    const copiedParams = JSON.parse(JSON.stringify(this.fetchDataParams));
    if (copiedParams.params) {
      Object.assign(copiedParams.params, this.fetchParams);
    } else {
      copiedParams.params = this.fetchParams;
    }
    return axiosWrap(copiedParams)
      .then((res: any) => {
        this.lists = this.getContactLists(res.content);
        this.itemsLength = res.totalElements;
      })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.loading = false;
        this.$store.commit("overlay", false);
      });
  }

  getDate(timestamp: number) {
    if (!timestamp) {
      return "";
    }
    const date = new Date(timestamp);
    const day = this.checkValueFormat(date.getDay());
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
    if (this.propsToConvertDate && this.propsToConvertDate.length) {
      return list.map(item => {
        this.propsToConvertDate.forEach(prop => {
          item[prop] = this.getDate(item[prop]);
        });
        return item;
      });
    }
    return list;
  }

  paginate(p: Pagination) {
    if (p.page === this.paginationMemorize.page && p.itemsPerPage === this.paginationMemorize.itemsPerPage) return;
    this.paginationMemorize.page = p.page;
    this.paginationMemorize.itemsPerPage = p.itemsPerPage;
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

  onDialogOpen(id: number) {
    this.isDialogShown = true;
    this.itemToRemove = id;
  }

  onDialogClose(answer: boolean) {
    this.isDialogShown = false;
    if (answer && this.itemToRemove) {
      this.itemToRemove === 0
        ? this.deleteAll()
        : this.deleteList(this.itemToRemove);
      this.itemToRemove = null;
    }
  }

  deleteAll() {
    return;
  }

  deleteList(id: number) {
    const copiedParams = JSON.parse(JSON.stringify(this.deleteItemParams));
    copiedParams.url = copiedParams.url + id;
    if (copiedParams.meta && copiedParams.meta.param1 != null) {
      copiedParams.url = copiedParams.url + "/" + copiedParams.meta.param1;
    }
    axiosWrap(copiedParams)
      .then((res: any) => {
        const index = this.lists.findIndex(
          (item: { id: number }) => item.id === id
        );
        this.lists.splice(index, 1);
        this.itemsLength = this.itemsLength - 1;
      })
      .catch(() => {
        this.$store.commit("snackbar", "Something vent wrong");
      })
      .finally(() => {
        this.loading = false;
        this.$store.commit("overlay", false);
      });
  }

  edit(id: number) {
    this.$emit("edit", id);
  }
}
</script>
