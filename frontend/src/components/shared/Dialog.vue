<template>
  <v-dialog v-model="isShown" max-width="300">
    <v-card>
      <v-card-title class="headline">{{ title }}</v-card-title>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="cancel">
          Cancel
        </v-btn>
        <v-btn color="primary" text @click="agree">
          Yes
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
@Component
export default class Dialog extends Vue {
  @Prop({ default: false })
  isShown!: boolean;
  @Prop({ default: "" })
  title!: string;

  agree() {
    this.$emit("onClose", true);
  }

  cancel() {
    this.$emit("onClose", false);
  }
}
</script>
