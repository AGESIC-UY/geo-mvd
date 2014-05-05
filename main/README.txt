
Ejecutar una aplicacion
=======================

- Se necesita un JBoss con el componente servidor instalado (SIGServerEAR)
El .ear se encuentra en en el proyecto SIGServerEAR/target/SIGServerEAR.ear

- Configurar una aplicacion con un juego de archivos XML en la siguiente carpeta:

${jboss.server.config.url}/apps/_APP/xml

APP = nombre de la aplicacion

- Ejecutar aplicacion en SIGServerEditorSample

El main esta en la clase imm.gis.run.SimpleRunEditor y se debe cambiar el nombre de la 
aplicacion que aparece en el archivo por el configurado en el paso 2.
Luego correr la aplicacion con el goal mvn exec:java 