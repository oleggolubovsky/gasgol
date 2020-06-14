<template>
  <v-avatar class="logo" tile :size="size">
    <v-img src="../../assets/whatsapp-ok.png"></v-img>
  </v-avatar>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";

@Component
export default class Logo extends Vue {
  @Prop({ default: 48 })
  size!: number;
}
</script>
