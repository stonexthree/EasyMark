<template>
  <div class="component-root">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script lang="ts">
export default {
  name: "CircleBackground"
}
</script>
<script setup lang="ts">
import {ref,Ref,onMounted} from 'vue'

const canvas= ref<HTMLCanvasElement|null>(null);

</script>

<style scoped>
canvas{
  width: 100%;
  height: 100%;
}
</style>