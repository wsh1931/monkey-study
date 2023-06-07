<template> 
  <div id="app" style="background-color: #EEEEEE;">
      <NavBar/>
      <router-view/>
    </div>
</template>

<script>
import NavBar from '@/components/NavBar'

export default{
  name: "App",
  
  components: {
    NavBar,
  },
  data() {
    return {
      
    }
  },

  
  
}
</script>

<style>
/* 除去router-link下划线 */
a{
  text-decoration: none;
}
.router-link-active {
  text-decoration: none;
}

</style>
