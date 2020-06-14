<template>
  <v-content>
    <v-container fluid fill-height>
      <v-layout align-center justify-center>
        <v-flex xs12 sm8 md4>
          <div>
            <h3>You've got mail!</h3>
          </div>
          <p>
            We have sent an email to
            <router-link to>{{ email }}</router-link> with a link for activating
            account and proving you are human.
          </p>
          <p class="underline">
            Didn't get anything?
            <br />
            <v-btn small link color="primary" @click="$emit('resend-email')"
              >Resend email</v-btn
            >
            or check spam folder
          </p>
        </v-flex>
      </v-layout>
    </v-container>
  </v-content>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
@Component
export default class RegistrationInfo extends Vue {
  @Prop({ default: "" })
  email!: string;
}
</script>
