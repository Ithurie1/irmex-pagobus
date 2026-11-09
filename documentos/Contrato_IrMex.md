---
title: "CONTRATO DE PRESTACIÓN DE SERVICIOS DE DESARROLLO DE SOFTWARE"
subtitle: "Proyecto IrMex — Sistema Integrado de Transporte"
---

Contrato que celebran, por una parte, **[NOMBRE O RAZÓN SOCIAL DEL CLIENTE]**, y por la otra, **el Equipo de Desarrollo IrMex ("Equipo 2 Haitianos")**, con efectos a partir del **9 de octubre de 2026**, sujeto a las siguientes declaraciones y cláusulas.

---

# 1. PARTES

## 1.1 Cliente (en adelante, "EL CLIENTE")

| Dato | Información |
|---|---|
| Nombre / razón social | [____] |
| RFC | [____] |
| Representante legal | [____] |
| Domicilio | [____] |
| Correo electrónico | [____] |
| Teléfono | [____] |
| Proyecto / sistema | Sistema integrado de transporte |

## 1.2 Contratado (en adelante, "EL CONTRATADO")

| Dato | Información |
|---|---|
| Nombre del equipo | Equipo IrMex ("Equipo 2 Haitianos") |
| Representante / Líder de Proyecto | Héctor Mauricio García |
| RFC / persona física o moral | [____] |
| Domicilio | [____] |
| Correo electrónico | [____] |
| Teléfono | [____] |

**Integrantes del equipo y roles:**

| Integrante | Roles |
|---|---|
| Héctor Mauricio García | Líder de Proyecto, Analista, Modelador UML y responsable de la Reserva de Riesgos |
| Hazziel Enrique Ramírez | Diseñador de Interfaz y Tester |
| Omar Hazael Pérez | Diseñador de Base de Datos y Asegurador de Calidad |
| Jorge Iván Muñiz | Arquitecto de Software y Codificador |

## 1.3 Datos generales del contrato

| Dato | Información |
|---|---|
| Nombre del proyecto | IrMex — Sistema Integrado de Transporte |
| Fecha de firma | [____] |
| Lugar de firma | [CIUDAD, ESTADO] |
| Vigencia | Del 9 de octubre de 2026 al 23 de noviembre de 2026 (fecha de entrega), salvo ampliación conforme a las cláusulas 4.2 y 6 |
| Versión del contrato | 1.0 |

---

# 2. OBLIGACIONES

## 2.1 Servicio / Producto

EL CONTRATADO se obliga a diseñar, desarrollar, probar, instalar y entregar a EL CLIENTE la plataforma **IrMex**, un sistema web de gestión integral de transporte estudiantil (tarjetas de tránsito, saldo, recargas, renovación documental, soporte y auditoría), construido con FastAPI (Python), PostgreSQL y un frontend en HTML, CSS y JavaScript.

### 2.1.1 Objetivos

1. Eliminar la gestión manual y descentralizada de las tarjetas de transporte estudiantil mediante un único backend RESTful.
2. Permitir al estudiante registrarse, consultar y administrar su tarjeta, recargar saldo, renovar su vigencia con documentos y levantar solicitudes de soporte.
3. Dotar al administrador de un panel de control con reportes financieros y operativos, gestión de tarjetas, estaciones, roles y auditoría de accesos.
4. Habilitar la lectura de plásticos físicos Pagobús mediante lectores NFC/RFID USB y la notificación de saldo por mensajería.
5. Entregar el sistema desplegado, documentado y con capacitación para su operación y mantenimiento.

### 2.1.2 Alcance

El alcance comprende los siguientes módulos, tal como están definidos en el cronograma:

| Módulo | Contenido |
|---|---|
| **MOD-01 — Autenticación y Cuentas** | Registro de usuarios, inicio de sesión con JWT y contraseñas con hash (bcrypt), perfil, recuperación y restablecimiento de contraseña, roles y privilegios |
| **MOD-02 — Tarjetas y Operaciones** | Alta, consulta, bloqueo y baja lógica de tarjetas; recarga de saldo; cobro de pasaje (simulador de validador); historial de movimientos; integración con lectores NFC/RFID |
| **MOD-03 — Trámites Escolares** | Solicitud de renovación con carga de documentos, bóveda de documentos seguros, aprobación o rechazo y asignación de vigencia |
| **MOD-04 — Red de Transporte** | Catálogo y gestión de estaciones (operativa / mantenimiento) |
| **MOD-05 — Soporte y Auditoría** | Buzón de tickets con evidencias y respuestas, bitácora de accesos, reportes y exportación a CSV |

Adicionalmente, el alcance incluye:

- Notificaciones de saldo mediante la API de mensajería (WhatsApp Cloud API, capa gratuita / sandbox).
- Publicación de la aplicación móvil en Google Play (licencia de desarrollador incluida en el presupuesto).
- Despliegue en servidor cloud (Render) con base de datos PostgreSQL, y dominio web institucional.
- Documentación técnica: diagramas UML, modelo relacional, documentación de código, plan y casos de prueba, y manuales de usuario.
- Capacitación al personal del CLIENTE, instalación y mantenimiento inicial.
- Suministro de hardware conforme a la cláusula 3.2: 4 laptops, 2 lectores NFC/RFID y 100 tarjetas RFID en blanco.

**Exclusiones.** No forman parte del alcance, salvo pacto escrito conforme a la cláusula 4.2 (control de cambios): la integración con una pasarela de pago bancaria real; el hosting, dominio y licencias posteriores al primer año; el soporte o mantenimiento evolutivo posterior a la entrega; y cualquier funcionalidad no listada en esta cláusula.

### 2.1.3 Visión

IrMex aspira a ser la plataforma única, confiable y escalable para la administración del transporte estudiantil: que cada estudiante gestione su tarjeta y su saldo de forma digital y transparente, y que la institución cuente con información auditable y en tiempo real para operar y mejorar el servicio, sobre una base técnica que pueda crecer hacia nuevos módulos, rutas y medios de pago.

## 2.2 Cronograma

### 2.2.1 Fechas

Duración total del proyecto: **31.5 días** (520 horas de trabajo), del **9 de octubre de 2026 al 23 de noviembre de 2026**.

| Etapa / actividad | Horas | Inicio | Fin |
|---|---:|---|---|
| **Administración de proyecto**: planeación, monitoreo y control | 100 | 09/10/26 | 16/10/26 |
| **Análisis**: administración de requerimientos y diagramas UML | 20 | 16/10/26 | 19/10/26 |
| **Diseño**: interfaces, base de datos, arquitectura, diagramas UML | 140 | 19/10/26 | 30/10/26 |
| **Codificación**: MOD-01 (30/10–02/11), MOD-02 (02/11–03/11), MOD-03 (03/11–04/11), MOD-04 (04/11–05/11), MOD-05 (05/11–06/11) | 70 | 30/10/26 | 06/11/26 |
| **Pruebas**: documentación (plan y casos) y pruebas unitarias e integración | 60 | 06/11/26 | 11/11/26 |
| **Implantación**: manuales y capacitación; instalación y mantenimiento | 50 | 11/11/26 | 16/11/26 |
| **Cierre — reserva de riesgos** | 80 | 16/11/26 | 23/11/26 |
| **Entrega del proyecto (hito)** | 0 | **23/11/26** | **23/11/26** |

Cuando la fecha de firma sea posterior al 9 de octubre de 2026, las partes reconocen que los trabajos ejecutados desde esa fecha se rigen por este contrato.

### 2.2.2 Costo (desglosado)

**A) Mano de obra** (cifras en MXN, sin utilidad ni IVA)

| Etapa | Actividad | Horas | Costo/hora | Subtotal |
|---|---|---:|---:|---:|
| Análisis | Administración de requerimientos | 10 | 60 | 600 |
| Análisis | Diagramas UML (análisis) | 10 | 60 | 600 |
| Diseño | Interfaces | 40 | 100 | 4,000 |
| Diseño | Base de datos | 30 | 150 | 4,500 |
| Diseño | Arquitectura | 40 | 180 | 7,200 |
| Diseño | Diagramas UML (diseño) | 30 | 70 | 2,100 |
| Codificación | Código fuente, APIs y documentación de código | 70 | 180 | 12,600 |
| Pruebas | Documentación (plan y casos de prueba) | 30 | 100 | 3,000 |
| Pruebas | Pruebas unitarias e integración | 30 | 170 | 5,100 |
| Implantación | Manuales y capacitación | 20 | 90 | 1,800 |
| Implantación | Instalación y mantenimiento | 30 | 90 | 2,700 |
| Administración de proyecto | Planeación, monitoreo y control | 100 | 190 | 19,000 |
| Administración de proyecto | Reserva de riesgos | 80 | 180 | 14,400 |
| | **Total mano de obra** | **520** | | **77,600** |

Resumen por etapa: Análisis $1,200 · Diseño $17,800 · Codificación $12,600 · Pruebas $8,100 · Implantación $4,500 · Administración de proyecto $33,400.

**B) Hardware, software y otros**

| Tipo | Concepto | Cant. | Costo unitario | Subtotal |
|---|---|---:|---:|---:|
| Hardware | Laptops (Intel Core i5 / AMD Ryzen 5, 16 GB RAM, SSD 512 GB, Windows 11) — *se entregan a EL CLIENTE* (costo estimado) | 4 | 14,000 | 56,000 |
| Hardware | Lector NFC/RFID USB (ej. ACR122U) | 2 | 750 | 1,500 |
| Hardware | Tarjetas RFID en blanco (Mifare 13.56 MHz) | 100 | 15 | 1,500 |
| Software | Servidor cloud Render (0.5 vCPU, 512 MB) y PostgreSQL Basic-256mb, plan anual (≈13 USD/mes, estimado) | 1 | 3,000 | 3,000 |
| Software | Dominio web institucional (.edu.mx o .com) | 1 | 400 | 400 |
| Software | Licencia de desarrollador Google Play Console | 1 | 500 | 500 |
| Software | API de mensajería WhatsApp Cloud (capa gratuita / sandbox) | 1 | 0 | 0 |
| Software | Ecosistema de desarrollo (GitHub, Docker, FastAPI, PostgreSQL) | 1 | 0 | 0 |
| Software | IDE Visual Studio Code | 4 | 0 | 0 |
| Otros | Costos indirectos fijos (agua, luz, renta) | 4 | 500 | 2,000 |
| | **Total hardware, software y otros** | | | **64,900** |

**C) Precio total del contrato**

| Concepto | Importe (MXN) |
|---|---:|
| Mano de obra | 77,600.00 |
| Hardware, software y otros | 64,900.00 |
| **Costo de desarrollo** | **142,500.00** |
| Utilidad (30%) | 42,750.00 |
| Subtotal (desarrollo + utilidad) | 185,250.00 |
| IVA (16% sobre el subtotal) | 29,640.00 |
| **PRECIO TOTAL DEL CONTRATO** | **$214,890.00** |

*(Doscientos catorce mil ochocientos noventa pesos 00/100 M.N.)*

### 2.2.3 Modalidad de pago

El precio se paga en dos exhibiciones, en moneda nacional, mediante transferencia electrónica a la cuenta que EL CONTRATADO indique por escrito:

| Pago | % | Importe (MXN) | Momento |
|---|---:|---:|---|
| Anticipo | 70% | $150,423.00 | A la firma del contrato |
| Liquidación | 30% | $64,467.00 | Al término del trabajo, contra la entrega y aceptación del sistema |
| **Total** | 100% | **$214,890.00** | |

Cada pago se acompaña del comprobante fiscal (CFDI) correspondiente.

---

# 3. OBLIGACIONES ADICIONALES DE LAS PARTES

## 3.1 Del Contratado

a) Ejecutar el servicio conforme al alcance, cronograma y estándares de calidad aquí pactados, aplicando el plan de pruebas y las acciones preventivas y correctivas del control de riesgos.
b) Mantener el código en repositorio con control de versiones y respaldos (riesgo RI-02).
c) Informar por escrito a EL CLIENTE de cualquier desviación relevante de tiempo, costo o esfuerzo.
d) Entregar código fuente, documentación, manuales y accesos a la infraestructura contratada.
e) Guardar confidencialidad sobre la información y los datos personales a los que tenga acceso.

## 3.2 Del Cliente

a) Pagar el precio en los términos de la cláusula 2.2.3.
b) Proporcionar oportunamente la información, accesos, aclaraciones y decisiones necesarias para el desarrollo, y designar un responsable de seguimiento y aceptación.
c) Revisar los entregables y emitir su aceptación o sus observaciones por escrito dentro de **5 días hábiles** posteriores a cada entrega; transcurrido ese plazo sin respuesta, se tendrán por aceptados.
d) Recibir el hardware descrito en el apartado 2.2.2-B y responsabilizarse de su resguardo.
e) Cubrir las renovaciones de hosting, dominio y licencias posteriores al primer año.

---

# 4. DERECHOS

## 4.1 Paga

EL CONTRATADO tiene derecho a recibir el precio total del contrato de **$214,890.00 MXN**, conforme a este esquema:

- **Anticipo del 70%** ($150,423.00), pagadero a la firma del contrato.
- **30% restante** ($64,467.00), pagadero al término del trabajo, contra la entrega del sistema y su aceptación.

El retraso en cualquiera de los pagos faculta a EL CONTRATADO a suspender los trabajos y a ajustar el cronograma por el tiempo equivalente al retraso. En caso de mora, se pagarán intereses moratorios a razón de **[____]% mensual** sobre el saldo vencido.

EL CLIENTE tiene derecho a recibir el producto, los entregables y la capacitación descritos en la cláusula 2, a la calidad y funcionalidad pactadas, a recibir información periódica del avance y a que se corrijan sin costo los defectos que se reporten dentro del periodo de garantía.

**Garantía.** EL CONTRATADO corregirá sin costo los errores de funcionamiento atribuibles al desarrollo, reportados dentro de los **30 días naturales** posteriores a la entrega.

## 4.2 Control de cambios

Todo cambio al alcance, funcionalidades, cronograma o costo se gestiona así:

1. **Solicitud escrita** de cualquiera de las partes, describiendo el cambio.
2. **Análisis de impacto** por EL CONTRATADO en tiempo, costo (con las tarifas por hora de la cláusula 2.2.2-A) y esfuerzo en horas, en un máximo de **3 días hábiles**.
3. **Reunión de aclaración** de alcances con EL CLIENTE.
4. **Aprobación por escrito** de ambas partes. Ningún cambio se ejecuta ni se cobra sin esa aprobación.
5. **Actualización** del cronograma, presupuesto y versión del documento, registrada en la bitácora de control de cambios. Los cambios aprobados con costo adicional se facturan por separado.

Los requerimientos que cambien o no estén claros se tratan como riesgo del proyecto (RI-03) y se resuelven por este mismo procedimiento. La reserva de riesgos (80 horas, $14,400) cubre contingencias propias del desarrollo, no cambios de alcance solicitados por EL CLIENTE.

---

# 5. MÉTRICAS

Las siguientes métricas son la base de seguimiento y aceptación del proyecto:

| Métrica | Valor acordado |
|---|---|
| **Tiempo (días/meses)** | 31.5 días de calendario de trabajo (09/10/26 al 23/11/26); presupuestado en **1.5 meses** (30 días) |
| **Costo ($)** | Desarrollo $142,500.00 · Utilidad $42,750.00 · Subtotal $185,250.00 · IVA $29,640.00 · **Total $214,890.00 MXN** |
| **Esfuerzo (horas)** | **520 horas** en total (Admin. de proyecto 180, Diseño 140, Codificación 70, Pruebas 60, Implantación 50, Análisis 20) |

Equipo de 4 personas, a medio tiempo. Se medirá el avance semanal contra estas métricas (riesgo RI-04); una desviación mayor a **10%** en tiempo, costo o esfuerzo debe notificarse y gestionarse según la cláusula 4.2.

---

# 6. ENTREGA Y ACEPTACIÓN

La entrega del proyecto se programa para el **23 de noviembre de 2026**. Se considera aceptada cuando EL CLIENTE verifica que los módulos MOD-01 a MOD-05 funcionan conforme al plan de pruebas, o por el vencimiento del plazo de revisión previsto en la cláusula 3.2-c. Los retrasos atribuibles a EL CLIENTE (falta de información, de pagos o de decisiones) amplían el plazo por el tiempo equivalente.

# 7. PROPIEDAD INTELECTUAL

EL CONTRATADO (Equipo IrMex, "Equipo 2 Haitianos") conserva la **titularidad absoluta** del código fuente, la arquitectura, la documentación técnica y demás elementos desarrollados en este proyecto. Una vez cubierto el 100% del precio, EL CONTRATADO otorga a EL CLIENTE **una licencia de uso perpetua** del sistema IrMex para sus fines operativos. Dicha licencia no transfiere la titularidad ni el derecho de ceder, sublicenciar, comercializar o modificar el código fuente, salvo pacto escrito conforme a la cláusula 4.2.

Los componentes de terceros y de código abierto se rigen por sus propias licencias.

---

# 8. CONFIDENCIALIDAD Y DATOS PERSONALES

Las partes mantendrán confidencial la información no pública que intercambien. Los datos personales de estudiantes (nombre, CURP, domicilio, documentos de identidad) se tratarán conforme a la legislación mexicana aplicable en materia de protección de datos personales, y EL CLIENTE es el responsable del tratamiento y de su aviso de privacidad. La obligación subsiste **2** años tras la terminación.

# 9. TERMINACIÓN

Cualquiera de las partes puede rescindir el contrato por incumplimiento grave de la otra, previo aviso escrito con **5 días hábiles** para subsanar. En caso de terminación anticipada, EL CLIENTE pagará el trabajo realizado hasta esa fecha, calculado con las tarifas de la cláusula 2.2.2-A, más los costos de hardware y servicios ya adquiridos; el anticipo se aplica a ese monto.

# 10. LIMITACIÓN DE RESPONSABILIDAD

La responsabilidad total de EL CONTRATADO se limita al monto efectivamente pagado por EL CLIENTE bajo este contrato, y no incluye daños indirectos, lucro cesante ni fallas derivadas de servicios de terceros (Render, WhatsApp Cloud API, Google Play).

# 11. JURISDICCIÓN Y LEY APLICABLE

Este contrato se rige por las leyes de **[ESTADO], México**. Para su interpretación y cumplimiento, las partes se someten a los tribunales competentes de **[CIUDAD, ESTADO]**, renunciando a cualquier otro fuero que pudiera corresponderles.

---

# FIRMAS

Leído el contrato y enteradas las partes de su contenido y alcance, lo firman en **[CIUDAD, ESTADO]**, a **[__] de [________] de 2026**.

| EL CLIENTE | EL CONTRATADO |
|---|---|
| | |
| ____________________________ | ____________________________ |
| [Nombre y cargo] | Héctor Mauricio García |
| | Líder de Proyecto, Equipo IrMex |

| Testigo 1 | Testigo 2 |
|---|---|
| ____________________________ | ____________________________ |
| [Nombre] | [Nombre] |
