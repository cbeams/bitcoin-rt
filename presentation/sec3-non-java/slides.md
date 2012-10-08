!SLIDE subsection
# Non-Java Solutions

!SLIDE small incremental bullets
# Blah
* blah
* blah
* blah

!SLIDE smaller
# Code Sample

    @@@ java
    public class DispatcherServletInitializer extends
          AbstractAnnotationConfigDispatcherServletInitializer {

      protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class };
      }

      protected Class<?>[] getServletConfigClasses() {
          return new Class<?>[] { WebMvcConfig.class };
      }

      protected String[] getServletMappings() {
          return new String[] { "/" };
      }

      protected Filter[] getServletFilters() {
          return new Filter[] { new OpenSessionInViewFilter() };
      }

    }

