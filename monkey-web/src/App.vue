<template>
  <NavBar/>
  <router-view/>
</template>

<script>
import NavBar from '@/components/NavBar'

export default ({
    components: {
      NavBar,
    },

    setup() {
      
    }
})
</script>

<style>
</style>
