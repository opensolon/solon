<script lang="ts" setup>

import {useApplication} from "../../hooks/application.ts";
import {useI18n} from "vue-i18n";

const application = useApplication().currentApplication()

const {t} = useI18n()

</script>

<template>
  <a-alert v-if="!application?.showSecretInformation" :title="t('application.environment.alert.hidden_sensitive_information.title')" closable
           type="warning">
    {{ t("application.environment.alert.hidden_sensitive_information.content") }}
  </a-alert>
  <div class="background">
    <a-grid :colGap="12" :cols="1" :rowGap="16">
      <a-grid-item>
        <a-list>
          <template #header>
            {{ t("application.environment.system_environment_variable.title") }}
          </template>
          <a-list-item v-for="(v,k) in application?.environmentInformation?.systemEnvironment" :key="k">
            <div class="list-item">
              <span>{{ k }}</span>
              <span class="list-item-value">{{ v }}</span>
            </div>
          </a-list-item>
        </a-list>
      </a-grid-item>
      <a-grid-item>
        <a-list>
          <template #header>
            {{ t("application.environment.system_properties.title") }}
          </template>
          <a-list-item v-for="(v,k) in application?.environmentInformation?.systemProperties" :key="k">
            <div class="list-item">
              <span>{{ k }}</span>
              <span class="list-item-value">{{ v }}</span>
            </div>
          </a-list-item>
        </a-list>
      </a-grid-item>
      <a-grid-item>
        <a-list>
          <template #header>
            {{ t("application.environment.application_configuration_properties.title") }}
          </template>
          <a-list-item v-for="(v,k) in application?.environmentInformation?.applicationProperties" :key="k">
            <div class="list-item">
              <span>{{ k }}</span>
              <span class="list-item-value">{{ v }}</span>
            </div>
          </a-list-item>
        </a-list>
      </a-grid-item>
    </a-grid>
  </div>
</template>

<style scoped>
.background {
  box-sizing: border-box;
  width: 100%;
  padding: 40px;
}

.list-item {
  display: grid;
  grid-template-columns: 30% 70%;
  grid-column-gap: 10px;
}

@media (max-width: 1024px) {
  .list-item {
    display: flex;
    flex-direction: column;
  }
}

.list-item-value {
  color: var(--color-text-3);
}

</style>