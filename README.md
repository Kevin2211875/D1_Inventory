# D1_Stock – Sistema de Gestión con Java Spring Boot

## Descripcion
Aplicación web desarrollada con el objetivo de gestionar información relacionada con personas, productos y tipos de documentos. Implementa una arquitectura basada en capas (modelo, servicio, repositorio, controlador) y está estructurada según el patrón MVC. Además, ofrece una API REST que permite interactuar con el sistema de manera flexible, ideal para integraciones externas o desarrollo de frontends desacoplados.

## Características principales:
- Gestión de productos y sus detalles.

- Registro y administración de personas con su tipo de documento.

- Operaciones CRUD para todas las entidades principales.

- API REST lista para integración con frontends modernos.

- Separación de capas (modelo, servicio, repositorio, controlador).

- Estructura clara de controladores MVC y controladores REST para posibles integraciones externas.

- Organización clara por paquetes: controller, service, repository, model y rest.

- Estructura modular para facilitar mantenimiento y escalabilidad.

## Tecnologías utilizadas
**Backend:** *(Java - Spring Boot)*.
**Lenguaje:** *Java*.

**Paradigma**: Programación orientada a objetos (POO)

**Arquitectura**: Modelo - Vista - Controlador (MVC)

**Acceso a datos**: Spring Data JPA

**Servicios REST**: Controladores REST para las entidades principales

**Controladores**: Clases en el paquete controller y rest para gestión web y API REST.

**Persistencia**: Interfaces en el paquete repository (utiliza Spring Data JPA).

**Modelo**: Clases como Producto, Persona, Tipodocumento.

## Estado del proyecto
- Funcionalidad backend completamente implementada.

- Frontend no incluido en el repositorio (pensado para ser desacoplado o desarrollado por separado).


