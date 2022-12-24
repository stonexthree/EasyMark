<template>
<Milkdown :read-only="true" :preload="true" :doc-id='docId' />
</template>

<script lang="ts">
import Milkdown from "./Milkdown.vue";
export default {
  name: "ShowDoc",
  components: {Milkdown}
}
</script>

<script setup lang="ts">
import {useRoute} from 'vue-router'
const route = useRoute();
const docId =<string> route.params.docId;
</script>

<style scoped>
</style>