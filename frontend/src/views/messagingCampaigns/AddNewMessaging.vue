<template>
  <v-content class="list-container">
    <v-container fluid fill-height class="align-stretch flex-column">
      <v-container>
        <v-row justify="space-between" style="margin-top: -15px;">
          <v-col cols="12" md="5" class="text-left">
            <v-text-field
              label="New Messaging Campaigns"
              hide-details="auto"
              required
              append-icon="mdi-pencil"
              v-model="name"
              :disabled="allDisabled"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-row justify="space-between" style="margin-top: -15px;">
          <v-col cols="12" md="5" class="text-left">
            <v-breadcrumbs
              :items="breadcrumbsList"
              class="pl-4 pt-2"
              style="margin-left: -15px;"
            >
              <template v-slot:divider>
                <v-icon>mdi-chevron-right</v-icon>
              </template>
            </v-breadcrumbs>
          </v-col>
        </v-row>
      </v-container>
      <v-dialog v-model="setTemplateModal" max-width="700px" min-height="800px">
        <v-card>
          <v-card-text>
            <div flat>
              <v-toolbar-title class="main-title" style="font-size:18px;"
                >Add texts to the Veriables of Message Template</v-toolbar-title
              >
            </div>
            <div flat>
              <v-toolbar-title class="main-title" test-left
                >Template name</v-toolbar-title
              >
            </div>
            <!-- Var -->
            <v-row
              justify="space-between"
              v-for="variable in variables"
              :key="variable.name"
            >
              <v-col cols="12" md="3" class="text-left">
                <span class="v-toolbar__title"
                  >Variable
                  <v-chip class="ma-2" :color="variable.color" small label>{{
                    variable.name
                  }}</v-chip
                  >:</span
                >
              </v-col>
              <v-col cols="12" md="7" class="text-left">
                <v-text-field
                  :dir="variable.dir"
                  dense
                  v-model="variable.value"
                  @input="handleChangeVariable"
                  :disabled="variable.disabled"
                  hide-details="auto"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="2" class="text-right">
                <v-icon style="font-size: 30px;" @click="variable.dir = 'rtl'"
                  >mdi-format-align-right</v-icon
                >
                <v-icon style="font-size: 30px;" @click="variable.dir = 'ltr'"
                  >mdi-format-align-left</v-icon
                >
              </v-col>
            </v-row>
            <!-- /Var -->
            <v-divider></v-divider>

            <v-row justify="space-between">
              <v-col cols="12" md="12">
                <span class="main-title" style="font-size:18px;"
                  >Body text preview in WhatsApp Message Template</span
                >
              </v-col>
            </v-row>
            <v-textarea
              no-resize
              readonly
              :value="templatePreview"
            ></v-textarea>
            <div class="text-left" style="margin-top: -20px;">
              <span class="body-2"
                >{{ templatePreview.length }} symbols used from 70</span
              >
            </div>

            <div class="py-4 buttons-container">
              <v-btn
                class="mx-4"
                width="100px"
                color="primary"
                right
                outlined
                @click="cancelTemplateVariable"
              >
                Cancel
              </v-btn>
              <v-btn
                class="mx-4"
                width="100px"
                color="primary"
                right
                @click="saveTemplateVariable"
              >
                Save
              </v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-dialog>
      <v-container>
        <v-row justify="space-between" style="margin-top: -15px;">
          <v-col cols="12" md="5">
            <v-autocomplete
              :prepend-icon="'mdi-magnify'"
              :items="contactListItems"
              :loading="contactListLoading"
              :search-input.sync="contactListSearch"
              :value="contactListResultValue"
              v-model="contactListResult"
              item-text="name"
              return-object
              label="Search contact list"
              dense
              outlined
              :disabled="allDisabled"
            ></v-autocomplete>
          </v-col>
          <v-col cols="12" md="6">
            <v-row dense justify="space-between" style="margin-top: -10px;">
              <v-col cols="12" md="6" class="text-left">
                <v-btn
                  block
                  color="primary"
                  class="ma-2 new-list-btn main-btn"
                  @click="saveHandler"
                  :disabled="allDisabled || saveDisabled"
                >
                  Save
                </v-btn>
              </v-col>
              <v-col cols="12" md="6" class="text-right">
                <v-btn
                  v-if="!allDisabled"
                  block
                  color="success"
                  class="ma-2 new-list-btn main-btn"
                  @click="startMessagingHandler"
                  :disabled="startMessagingDisabled"
                >
                  Start Messaging
                </v-btn>
                <v-btn
                  v-if="allDisabled"
                  block
                  color="primary"
                  class="ma-2 new-list-btn main-btn"
                  @click="saveHandler"
                >
                  Copy Campaing
                </v-btn>
              </v-col>
            </v-row>
          </v-col>
        </v-row>

        <v-row justify="space-between" style="margin-top: -30px;">
          <v-col cols="12" md="5">
            <v-row justify="space-between">
              <v-col cols="12" md="6" class="text-left">
                <span class="v-toolbar__title">SMS Message:</span>
              </v-col>
              <v-col cols="12" md="4" class="text-right">
                <v-icon
                  :disabled="allDisabled"
                  style="font-size: 30px;"
                  @click="smsDir = 'rtl'"
                  >mdi-format-align-right</v-icon
                >
                <v-icon
                  :disabled="allDisabled"
                  style="font-size: 30px;"
                  @click="smsDir = 'ltr'"
                  >mdi-format-align-left</v-icon
                >
                <v-icon :disabled="allDisabled" style="font-size: 30px;"
                  >mdi-emoticon</v-icon
                >
              </v-col>
            </v-row>
            <v-textarea
              :dir="smsDir"
              outlined
              no-resize
              placeholder="Write something here..."
              v-model="sms"
              :disabled="allDisabled"
            ></v-textarea>
            <div class="text-left" style="margin-top: -30px;">
              <span class="body-2">{{ sms.length }} symbols used from 70</span>
            </div>
            <v-row justify="space-between" style="height: 45px;">
              <v-col cols="12" md="1" class="text-left">
                <v-switch
                  :disabled="allDisabled"
                  v-model="smsNotReceiveWatsApp"
                  style="margin-top: -5px;"
                />
              </v-col>
              <v-col cols="12" md="11" class="text-left">
                <span class="subtitle-2"
                  >Only for those who did not receive WhatsApp Message</span
                >
              </v-col>
            </v-row>
            <v-row justify="space-between" style="height: 45px;">
              <v-col cols="12" md="1" class="text-left">
                <v-switch
                  :disabled="allDisabled"
                  v-model="smsResendTomorrow"
                  style="margin-top: -5px;"
                />
              </v-col>
              <v-col cols="12" md="11" class="text-left">
                <span class="subtitle-2"
                  >Resend this tomorrow at the same time</span
                >
              </v-col>
            </v-row>
          </v-col>
          <v-col cols="12" md="6">
            <v-radio-group v-model="radioGroup" :disabled="allDisabled">
              <v-row>
                <v-col cols="12" md="1">
                  <v-radio value="1"></v-radio>
                </v-col>
                <v-col cols="12" md="11" style="margin-top: -32px;">
                  <v-row justify="space-between">
                    <v-col cols="12" md="12" class="text-left">
                      <span class="v-toolbar__title"
                        >Add the First WhatsApp Message of client:</span
                      >
                    </v-col>
                  </v-row>
                  <v-text-field
                    v-model="additionalFW"
                    placeholder="Write maximum 3 words here..."
                    outlined
                    dense
                    hide-details="auto"
                  ></v-text-field>
                  <div class="text-left">
                    <span class="body-2"
                      >Add the text, that the Contact will send to start
                      WhatsApp chat. Please enter maximum 3 words</span
                    >
                  </div>
                  <v-row justify="space-between" style="height: 45px;">
                    <v-col cols="12" md="1" class="text-left">
                      <v-switch
                        :disabled="allDisabled"
                        v-model="additionalFWReplaceLink"
                        style="margin-top: -5px;"
                      />
                    </v-col>
                    <v-col cols="12" md="11" class="text-left">
                      <span class="subtitle-2"
                        >Replace this link to unique short link</span
                      >
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12" md="1">
                  <v-radio value="2"></v-radio>
                </v-col>
                <v-col cols="12" md="11" style="margin-top: -32px;">
                  <v-row justify="space-between">
                    <v-col cols="12" md="12" class="text-left">
                      <span class="v-toolbar__title"
                        >Add Campaing Link to SMS Message:</span
                      >
                    </v-col>
                  </v-row>
                  <v-text-field
                    v-model="additionalLink"
                    placeholder="Write full campaing link here..."
                    outlined
                    dense
                    hide-details="auto"
                  ></v-text-field>
                  <div class="text-left">
                    <span class="body-2"
                      >Please enter the full link format:
                      http://www.domain.com</span
                    >
                  </div>
                  <v-row justify="space-between" style="height: 45px;">
                    <v-col cols="12" md="1" class="text-left">
                      <v-switch
                        :disabled="allDisabled"
                        v-model="additionalLinkReplaceLink"
                        style="margin-top: -5px;"
                      />
                    </v-col>
                    <v-col cols="12" md="11" class="text-left">
                      <span class="subtitle-2"
                        >Replace this link to unique short link</span
                      >
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="12" md="1">
                  <v-radio value="3"></v-radio>
                </v-col>
                <v-col cols="12" md="11" style="margin-top: -32px;">
                  <v-row justify="space-between">
                    <v-col cols="12" md="12" class="text-left">
                      <span class="v-toolbar__title"
                        >Nothing added to SMS Message</span
                      >
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </v-radio-group>
          </v-col>
        </v-row>

        <v-row justify="space-between" style="margin-top: -40px;">
          <v-col cols="12" md="5">
            <v-row justify="space-between">
              <v-col cols="12" md="6" class="text-left">
                <span class="v-toolbar__title">WhatsApp Reply Message:</span>
              </v-col>
              <v-col cols="12" md="4" class="text-right">
                <v-icon
                  style="font-size: 30px;"
                  :disabled="allDisabled"
                  @click="whatsAppReplayMessageDir = 'rtl'"
                  >mdi-format-align-right</v-icon
                >
                <v-icon
                  style="font-size: 30px;"
                  :disabled="allDisabled"
                  @click="whatsAppReplayMessageDir = 'ltr'"
                  >mdi-format-align-left</v-icon
                >
                <v-icon :disabled="allDisabled" style="font-size: 30px;"
                  >mdi-emoticon</v-icon
                >
              </v-col>
            </v-row>
            <v-textarea
              :dir="whatsAppReplayMessageDir"
              v-model="whatsAppReplayMessage"
              no-resize
              outlined
              placeholder="Write something here..."
              :disabled="allDisabled"
            ></v-textarea>
            <div class="text-left" style="margin-top: -30px;">
              <span class="body-2"
                >{{ whatsAppReplayMessage.length }} symbols used from 1024</span
              >
            </div>
            <v-file-input
              label="File input"
              :error="whatsAppReplayMessageFile1Error"
              :value="whatsAppReplayMessageFile1Value"
              @change="whatsAppReplayMessageFile1Change"
              :disabled="allDisabled"
            ></v-file-input>
            <div
              class="text-left"
              style="margin-top: -20px; margin-left: 30px;"
            >
              <span class="body-2"
                >Please insert any File that satisfy to Facebook policy</span
              >
            </div>
            <v-file-input
              label="File input"
              :error="whatsAppReplayMessageFile2Error"
              :value="whatsAppReplayMessageFile2Value"
              @change="whatsAppReplayMessageFile2Change"
              :disabled="allDisabled"
            ></v-file-input>
            <div
              class="text-left"
              style="margin-top: -20px; margin-left: 30px;"
            >
              <span class="body-2"
                >Please insert any File that satisfy to Facebook policy</span
              >
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <v-row justify="space-between">
              <v-col cols="12" md="12" class="text-left">
                <span class="v-toolbar__title">WhatsApp Template Message:</span>
              </v-col>
            </v-row>
            <v-select
              :prepend-icon="'mdi-magnify'"
              :items="templateItems"
              v-model="templateResult"
              label="Select WhatsApp Template"
              return-object
              item-text="name"
              @change="onChangeTemplate"
              dense
              outlined
              :disabled="allDisabled"
            ></v-select>
            <div
              class="text-left"
              style="margin-top: -20px; margin-left: 35px;"
            >
              <span class="body-2"
                >Please choose WhatsApp Message Template approved by
                Facebook</span
              >
            </div>
            <div class="text-left" style="margin-top: 0px; max-width: 250px;">
              <span class="body-1">{{ templateResultHeader }}</span>
              <br />
              <span class="body-1">{{ templateResultBody }}</span>
            </div>
            <v-file-input
              label="File input"
              :error="whatsAppReplayMessageTemplateFileError"
              :value="whatsAppReplayMessageTemplateFileValue"
              @change="whatsAppReplayMessageTemplateFileChange"
              :disabled="allDisabled"
            ></v-file-input>
            <div
              class="text-left"
              style="margin-top: -20px; margin-left: 30px;"
            >
              <span class="body-2"
                >Please insert file to Header of choosen WhatsApp Message
                Template</span
              >
            </div>
            <v-row justify="space-between" style="height: 45px;">
              <v-col cols="12" md="1" class="text-left">
                <v-switch
                  v-model="whatsAppTemplateMessageResendTomorrow"
                  style="margin-top: -5px;"
                  :disabled="allDisabled"
                />
              </v-col>
              <v-col cols="12" md="11" class="text-left">
                <span class="subtitle-2"
                  >Resend this tomorrow at the same time</span
                >
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-container>
    </v-container>
  </v-content>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import axios, { axiosWrap } from "@/http/axios";

@Component
export default class AddNewMessaging extends Vue {
  get startMessagingDisabled() {
    const smsEmpty = this.sms.trim() == "";
    const whatsAppReplayMessageEmpty = this.whatsAppReplayMessage.trim() == "";
    const templateResultEmpty = this.templateResult.id == 0;
    const nameEmpty = this.name.trim() == "";
    return (
      (smsEmpty && whatsAppReplayMessageEmpty && templateResultEmpty) ||
      nameEmpty
    );
  }

  get saveDisabled() {
    return this.name.trim() == "";
  }

  breadcrumbsList = [
    {
      text: "WAPP",
      disabled: false,
      href: "/",
    },
    {
      text: "Messaging Campaigns",
      disabled: false,
      href: "/messaging-campaigns",
    },
    {
      text: "New",
      disabled: true,
    },
  ];

  allDisabled = false;
  startMessagingDisabled1 = true;

  messageCampaingsId = 0;

  name = "";

  sms = "";
  smsDir = "ltr";
  smsNotReceiveWatsApp = false;
  smsResendTomorrow = false;

  radioGroup = "3";

  additionalFW = "";
  additionalFWReplaceLink = false;

  additionalLink = "";
  additionalLinkReplaceLink = false;

  whatsAppReplayMessage = "";
  whatsAppReplayMessageDir = "ltr";

  whatsAppTemplateMessageResendTomorrow = false;

  //========================= work with file =============================

  whatsAppReplayMessageFile1 = "";
  whatsAppReplayMessageFile1Value = undefined as any;
  whatsAppReplayMessageFile1Link = "";
  whatsAppReplayMessageFile1Error = false;

  whatsAppReplayMessageFile2 = "";
  whatsAppReplayMessageFile2Value = undefined as any;
  whatsAppReplayMessageFile2Link = "";
  whatsAppReplayMessageFile2Error = false;

  whatsAppReplayMessageTemplateFile = "";
  whatsAppReplayMessageTemplateFileValue = undefined as any;
  whatsAppReplayMessageTemplateFileLink = "";
  whatsAppReplayMessageTemplateFileError = false;

  appliedImageFormat = [
    "bmp",
    "dib",
    "heic",
    "heif",
    "iff",
    "jfif",
    "jp2",
    "jpe",
    "jpeg",
    "jpg",
    "png",
    "psd",
    "tif",
    "tiff",
    "wbmp",
    "webp",
    "xbm",
  ];

  async whatsAppReplayMessageFile1Change(file: any) {
    if (!file) {
      this.whatsAppReplayMessageFile1 = "";
      this.whatsAppReplayMessageFile1Value = undefined as any;
      this.whatsAppReplayMessageFile1Link = "";
      this.whatsAppReplayMessageFile1Error = false;
      return;
    }

    this.whatsAppReplayMessageFile1 = file;
    const type = file.name
      .split(".")
      .pop()
      .toLowerCase();
    if (this.appliedImageFormat.includes(type)) {
      this.whatsAppReplayMessageFile1Error = false;
      const link = await this.uploadIMG(this.whatsAppReplayMessageFile1);
      this.whatsAppReplayMessageFile1Link = link;
    } else {
      this.whatsAppReplayMessageFile1Error = true;
      this.whatsAppReplayMessageFile1Link = "";
    }
  }

  async whatsAppReplayMessageFile2Change(file: any) {
    if (!file) {
      this.whatsAppReplayMessageFile2 = "";
      this.whatsAppReplayMessageFile2Value = undefined as any;
      this.whatsAppReplayMessageFile2Link = "";
      this.whatsAppReplayMessageFile2Error = false;
      return;
    }

    this.whatsAppReplayMessageFile2 = file;
    const type = file.name
      .split(".")
      .pop()
      .toLowerCase();
    if (this.appliedImageFormat.includes(type)) {
      this.whatsAppReplayMessageFile2Error = false;
      const link = await this.uploadIMG(this.whatsAppReplayMessageFile1);
      this.whatsAppReplayMessageFile2Link = link;
    } else {
      this.whatsAppReplayMessageFile2Error = true;
      this.whatsAppReplayMessageFile2Link = "";
    }
  }

  async whatsAppReplayMessageTemplateFileChange(file: any) {
    if (!file) {
      this.whatsAppReplayMessageTemplateFile = "";
      this.whatsAppReplayMessageTemplateFileValue = undefined as any;
      this.whatsAppReplayMessageTemplateFileLink = "";
      this.whatsAppReplayMessageTemplateFileError = false;
    }

    this.whatsAppReplayMessageTemplateFile = file;
    const type = file.name
      .split(".")
      .pop()
      .toLowerCase();
    if (this.appliedImageFormat.includes(type)) {
      this.whatsAppReplayMessageTemplateFileError = false;
      const link = await this.uploadIMG(this.whatsAppReplayMessageFile1);
      this.whatsAppReplayMessageTemplateFileLink = link;
    } else {
      this.whatsAppReplayMessageTemplateFileError = true;
      this.whatsAppReplayMessageTemplateFileLink = "";
    }
  }

  uploadIMG(file: any): Promise<string> {
    return new Promise((resolve) => {
      const formData = new FormData();
      formData.append("file", file);

      axiosWrap({
        method: "post",
        headers: {
          "Content-Type": "multipart/form-data",
        },
        url: "/campaigns/upload",
        data: formData,
      })
        .then((res: any) => {
          if (!res) {
            resolve("");
          }
          resolve(res);
        })
        .catch((error) => {
          this.$store.commit(
            "snackbar",
            `Something vent wrong. ${error.response.data.errorMessage}`
          );
          resolve("");
        });
    });
  }

  //========================= work with template =============================
  setTemplateModal = false;

  cleanTemplateBody = "";

  cleanTemplateHeader = "";

  cleanTemplateHeaderMedia = true;

  templatePreview = "";

  templateResultHeader = "";
  templateResultBody = "";

  variables = [] as any[];

  templateItems = [{ name: "", id: 0 }];

  templateResult = { name: "", id: 0 };

  onChangeTemplate(result: any) {
    if (result.id == 0) {
      this.cancelTemplateVariable();
    } else {
      this.templateResult = result;
      this.getTemplateById(result.id);
    }
  }

  getAllTemplate() {
    axios.get("/templates").then((response) => {
      this.templateItems = [
        { name: "", id: 0 },
        ...response.data.content.map((item: any) => ({
          id: item.id,
          name: item.name,
        })),
      ];
    });
  }

  getTemplateById(id: number) {
    axios.get(`/templates/${id}`).then((response) => {
      this.cleanTemplateBody = response.data.jsonContent.body
        ? response.data.jsonContent.body
        : "";

      this.cleanTemplateHeader =
        response.data.jsonContent.header &&
        response.data.jsonContent.header.text
          ? response.data.jsonContent.header.text
          : "";

      this.cleanTemplateHeaderMedia = response.data.jsonContent.header
        ? response.data.jsonContent.header.media
        : true;

      this.parseTemplate([]);
    });
  }

  parseTemplate(startValues: { default: string; rtl: boolean }[]) {
    if (startValues.length == 0) {
      this.setTemplateModal = true;
    }

    this.variables = [];

    if (this.cleanTemplateHeader.includes("{{Name}}")) {
      this.variables.push({
        name: "AB",
        value: "Name",
        regex: "{{Name}}",
        color: "error",
        disabled: true,
        dir: "ltr",
        location: "header",
      });
    }

    if (this.cleanTemplateBody.includes("{{Name}}")) {
      this.variables.push({
        name: "AB",
        value: "Name",
        regex: "{{Name}}",
        color: "error",
        disabled: true,
        dir: "ltr",
        location: "body",
      });
    }

    for (let index = 1; index < 10; index++) {
      if (this.cleanTemplateHeader.includes("{{" + index + "}}")) {
        this.variables.push({
          name: index.toString(),
          value: "",
          regex: "{{[" + index + "]}}",
          color: "primary",
          disabled: false,
          dir: "ltr",
          location: "header",
        });
      }

      if (this.cleanTemplateBody.includes("{{" + index + "}}")) {
        this.variables.push({
          name: index.toString(),
          value: "",
          regex: "{{[" + index + "]}}",
          color: "primary",
          disabled: false,
          dir: "ltr",
          location: "body",
        });
      }
    }

    if (startValues.length != 0 && this.variables.length > 0) {
      const variablesWithoutName = this.variables.filter(
        (item) => item.name != "AB"
      );
      for (let index = 0; index < variablesWithoutName.length; index++) {
        variablesWithoutName[index].value = startValues[index].default;
        variablesWithoutName[index].dir = startValues[index].rtl
          ? "rtl"
          : "ltr";
      }
    }

    /*if (startValues.length != 0 && this.variables.length > 0) {
      const startIndex = this.variables[0].name == "AB" ? 1 : 0;
      for (let index = startIndex; index < this.variables.length; index++) {
        this.variables[index].value = startValues[index - startIndex].default;
        this.variables[index].dir = startValues[index - startIndex].rtl
          ? "rtl"
          : "ltr";
      }
    }*/

    let templateHeader = this.cleanTemplateHeader;
    let templateBody = this.cleanTemplateBody;

    this.variables
      .filter((item) => item.location == "header")
      .forEach((item: any) => {
        templateHeader = this.replaceVariable(
          templateHeader,
          item.regex,
          item.value
        );
      });

    this.variables
      .filter((item) => item.location == "body")
      .forEach((item: any) => {
        templateBody = this.replaceVariable(
          templateBody,
          item.regex,
          item.value
        );
      });

    this.templatePreview = templateHeader + "\r" + templateBody;

    if (startValues.length != 0) {
      this.saveTemplateVariable();
    }
  }

  handleChangeVariable() {
    let templateHeader = this.cleanTemplateHeader;
    let templateBody = this.cleanTemplateBody;

    this.variables
      .filter((item) => item.location == "header")
      .forEach((item: any) => {
        templateHeader = this.replaceVariable(
          templateHeader,
          item.regex,
          item.value
        );
      });

    this.variables
      .filter((item) => item.location == "body")
      .forEach((item: any) => {
        templateBody = this.replaceVariable(
          templateBody,
          item.regex,
          item.value
        );
      });

    this.templatePreview = templateHeader + "\r" + templateBody;
  }

  replaceVariable(string: string, regex: string, value: string) {
    return string.replace(new RegExp(regex, "g"), value);
  }

  saveTemplateVariable() {
    let templateHeader = this.cleanTemplateHeader;
    let templateBody = this.cleanTemplateBody;

    this.variables
      .filter((item) => item.location == "header")
      .forEach((item: any) => {
        templateHeader = this.replaceVariable(
          templateHeader,
          item.regex,
          item.value
        );
      });

    this.variables
      .filter((item) => item.location == "body")
      .forEach((item: any) => {
        templateBody = this.replaceVariable(
          templateBody,
          item.regex,
          item.value
        );
      });

    this.templateResultHeader = templateHeader;
    this.templateResultBody = templateBody;

    this.setTemplateModal = false;
  }

  cancelTemplateVariable() {
    this.templateResult = { name: "", id: 0 };

    this.templateResultHeader = "";
    this.templateResultBody = "";

    this.setTemplateModal = false;
  }

  //========================= work with contact list =============================

  contactListId = 0;
  contactListItems = [] as {
    name: string;
    id: number;
  }[];
  contactListResult = { name: "", id: 0 };
  contactListResultValue = { name: "", id: 0 };
  contactListSearch = "";
  contactListLoading = false;

  getAllContactList() {
    this.contactListLoading = true;

    axios
      .get("/contactLists", { params: { limit: 10 } })
      .then((response) => {
        this.contactListItems = response.data.content.filter(
          (item: any) => !!item.id
        );
      })
      .finally(() => {
        this.contactListLoading = false;
      });
  }

  @Watch("contactListResult", { immediate: true })
  contactListResultWatcher(result: any) {
    if (!result) return;

    this.contactListId = result.value;
  }

  @Watch("contactListSearch", { immediate: true })
  contactListSearchWatcher(search: any) {
    if (!search || this.contactListResult.name == search) return;

    this.contactListLoading = true;
    this.contactListResult.id = 0;
    axios
      .get("/contactLists/search", { params: { search, limit: 10 } })
      .then((response) => {
        this.contactListItems = response.data;
      })
      .finally(() => {
        this.contactListLoading = false;
      });
  }

  //========================= rusult =============================

  async saveHandler() {
    const savedCampaingId = await this.saveCampaigns();
    if (savedCampaingId != 0) {
      this.messageCampaingsId = savedCampaingId;
      this.$router.push({ path: `/messaging-campaigns` });
    }
  }

  saveCampaigns(): Promise<number> {
    const result = {
      campaignId:
        this.messageCampaingsId != 0 && !this.allDisabled
          ? this.messageCampaingsId
          : undefined,
      name: this.name,
      contactListId:
        this.contactListResult.id != 0 ? this.contactListResult.id : undefined,
      sms: {
        notReceiveWatsApp: this.smsNotReceiveWatsApp,
        resendTomorrow: this.smsResendTomorrow,
        nothingAddToSms: this.radioGroup == "3",
        additionalFW: {
          content: this.additionalFW,
          replaceLinkToUnique: this.additionalFWReplaceLink,
          selected: this.radioGroup == "1",
        },
        additionalLink: {
          content: this.additionalLink,
          replaceLinkToUnique: this.additionalLinkReplaceLink,
          selected: this.radioGroup == "2",
        },
        message: this.sms,
        rtl: this.smsDir == "rtl",
      },
      whatsAppReplayMessage: {
        mediaFiles: [] as string[],
        message: this.whatsAppReplayMessage,
        rtl: this.whatsAppReplayMessageDir == "rtl",
      },
      whatsAppTemplateMessage: {
        mediaFile: this.whatsAppReplayMessageTemplateFileLink,
        templateId: this.templateResult.id ? this.templateResult.id : undefined,
        resendTomorrow: this.whatsAppTemplateMessageResendTomorrow,
        components: [] as any[],
      },
    };

    const headerComponent = {
      type: "header",
      parameters: [] as any[],
    };

    const bodyComponent = {
      type: "body",
      parameters: [] as any[],
    };

    if (this.templateResult.id) {
      this.variables
        .filter((item) => item.location == "header")
        .forEach((item) => {
          if (item.name == "AB") {
            headerComponent.parameters.push({
              type: "text",
              text: "${{name}}",
              rtl: item.dir == "rtl",
            });
          } else {
            headerComponent.parameters.push({
              type: "text",
              text: item.value,
              rtl: item.dir == "rtl",
            });
          }
        });

      this.variables
        .filter((item) => item.location == "body")
        .forEach((item) => {
          if (item.name == "AB") {
            bodyComponent.parameters.push({
              type: "text",
              text: "${{name}}",
              rtl: item.dir == "rtl",
            });
          } else {
            bodyComponent.parameters.push({
              type: "text",
              text: item.value,
              rtl: item.dir == "rtl",
            });
          }
        });
    }

    result.whatsAppTemplateMessage.components.push(headerComponent);
    result.whatsAppTemplateMessage.components.push(bodyComponent);

    this.whatsAppReplayMessageFile1Link != "" &&
      result.whatsAppReplayMessage.mediaFiles.push(
        this.whatsAppReplayMessageFile1Link
      );
    this.whatsAppReplayMessageFile2Link != "" &&
      result.whatsAppReplayMessage.mediaFiles.push(
        this.whatsAppReplayMessageFile2Link
      );

    return new Promise((resolve) => {
      axiosWrap({
        method:
          this.messageCampaingsId == 0 ||
          (this.messageCampaingsId != 0 && this.allDisabled)
            ? "post"
            : "put",
        url: "/campaigns",
        data: result,
      })
        .then((res: any) => {
          if (res) {
            resolve(res);
          }
          resolve(0);
        })
        .catch((error) => {
          this.$store.commit(
            "snackbar",
            `Something vent wrong. ${error.response.data.errorMessage}`
          );
          resolve(0);
        });
    });
  }

  async startMessagingHandler() {
    let savedCampaingId = 0;
    if (this.messageCampaingsId == 0) {
      savedCampaingId = await this.saveCampaigns();
    } else {
      savedCampaingId = await this.saveCampaigns();
    }

    axios
      .post(`/campaigns/send/${savedCampaingId}`)
      .then((response) => {
        this.$router.push({ path: `/messaging-campaigns` });
      })
      .catch((error) => {
        this.$store.commit(
          "snackbar",
          `Something vent wrong. ${error.response.data.errorMessage}`
        );
      });
  }

  //========================= edit =============================
  getCampaingById(id: number) {
    axios.get(`/campaigns/${id}`).then((response) => {
      this.allDisabled = response.data.status != "OPENED";
      this.messageCampaingsId = response.data.campaignId;
      this.name = response.data.name;

      this.contactListResult.id = response.data.contactListId;
      if (response.data.contactListId) {
        axios
          .get(`/contactLists/${response.data.contactListId}`)
          .then((res) => {
            this.contactListResult = {
              name: res.data.name,
              id: res.data.id,
            };
            this.contactListItems = [] as {
              name: string;
              id: number;
            }[];
            this.contactListItems.push({
              name: res.data.name,
              id: res.data.id,
            });
          });
      }

      this.sms = response.data.sms.message;
      this.smsNotReceiveWatsApp = response.data.sms.notReceiveWatsApp;
      this.smsResendTomorrow = response.data.sms.resendTomorrow;
      this.smsDir = response.data.sms.rtl ? "rtl" : "ltr";

      this.additionalFW = response.data.sms.additionalFW.content;
      this.additionalFWReplaceLink =
        response.data.sms.additionalFW.additionalFWReplaceLink;

      this.additionalLink = response.data.sms.additionalLink.content;
      this.additionalLinkReplaceLink =
        response.data.sms.additionalLink.replaceLinkToUnique;

      if (response.data.sms.additionalFW.selected) {
        this.radioGroup = "1";
      }

      if (response.data.sms.additionalLink.selected) {
        this.radioGroup = "2";
      }

      if (response.data.sms.nothingAddToSms) {
        this.radioGroup = "3";
      }

      this.whatsAppReplayMessage = response.data.whatsAppReplayMessage.message;
      this.whatsAppReplayMessageDir = response.data.whatsAppReplayMessage.rtl
        ? "rtl"
        : "ltr";

      this.whatsAppTemplateMessageResendTomorrow =
        response.data.whatsAppTemplateMessage.resendTomorrow;

      this.whatsAppTemplateMessageResendTomorrow =
        response.data.whatsAppTemplateMessage.resendTomorrow;

      this.whatsAppReplayMessageTemplateFileValue = {
        name: response.data.whatsAppTemplateMessage.mediaFile,
      };
      this.whatsAppReplayMessageTemplateFileLink =
        response.data.whatsAppTemplateMessage.mediaFile;

      if (response.data.whatsAppTemplateMessage.templateId) {
        axios
          .get(`/templates/${response.data.whatsAppTemplateMessage.templateId}`)
          .then((res) => {
            this.templateResult = { name: res.data.name, id: res.data.id };

            this.cleanTemplateBody = res.data.jsonContent.body
              ? res.data.jsonContent.body
              : "";

            this.cleanTemplateHeader =
              res.data.jsonContent.header && res.data.jsonContent.header.text
                ? res.data.jsonContent.header.text
                : "";

            this.cleanTemplateHeaderMedia = res.data.jsonContent.header
              ? res.data.jsonContent.header.media
              : true;

            let headerParams = [] as { default: string; rtl: boolean }[];
            let bodyParams = [] as { default: string; rtl: boolean }[];

            response.data.whatsAppTemplateMessage.components.forEach(
              (item: any) => {
                if (item.type == "header") {
                  headerParams = item.parameters
                    .filter((item: any) => item.text != "${{name}}")
                    .map((item: any) => ({
                      default: item.text,
                      rtl: item.rtl,
                    }));
                }

                if (item.type == "body") {
                  bodyParams = item.parameters
                    .filter((item: any) => item.text != "${{name}}")
                    .map((item: any) => ({
                      default: item.text,
                      rtl: item.rtl,
                    }));
                }
              }
            );

            this.parseTemplate([...headerParams, ...bodyParams]);
          });
      }

      if (response.data.whatsAppReplayMessage.mediaFiles.length == 1) {
        this.whatsAppReplayMessageFile1Value = {
          name: response.data.whatsAppReplayMessage.mediaFiles[0],
        };
        this.whatsAppReplayMessageFile1Link =
          response.data.whatsAppReplayMessage.mediaFiles[0];
      }

      if (response.data.whatsAppReplayMessage.mediaFiles.length == 2) {
        this.whatsAppReplayMessageFile1Value = {
          name: response.data.whatsAppReplayMessage.mediaFiles[0],
        };
        this.whatsAppReplayMessageFile1Link =
          response.data.whatsAppReplayMessage.mediaFiles[0];

        this.whatsAppReplayMessageFile2Value = {
          name: response.data.whatsAppReplayMessage.mediaFiles[1],
        };
        this.whatsAppReplayMessageFile2Link =
          response.data.whatsAppReplayMessage.mediaFiles[1];
      }

      this.whatsAppReplayMessageTemplateFileValue = {
        name: response.data.whatsAppTemplateMessage.mediaFile,
      };
      this.whatsAppReplayMessageTemplateFileLink =
        response.data.whatsAppTemplateMessage.mediaFile;
    });
  }

  mounted() {
    this.getAllTemplate();
    this.getAllContactList();
    if (this.$route.params.id) {
      this.breadcrumbsList[2].text = this.$route.params.id;
      this.getCampaingById(+this.$route.params.id);
    }
  }
}
</script>

<style lang="scss"></style>
