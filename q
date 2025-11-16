[33mcommit c934cad3ecffdb8e3358e4ede53038a9e1e77836[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mmain[m[33m, [m[1;33mtag: [m[1;33mv0.4.0[m[33m, [m[1;31morigin/main[m[33m)[m
Merge: b6cba0c 975a33a
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Thu Nov 13 19:27:46 2025 -0600

    Merge feature/fast-intake-ui

[33mcommit 975a33ae7436e72bed3b8422ebe3b9de9960f0f0[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Thu Nov 13 18:51:17 2025 -0600

    feat(fast-intake-ui): alta rapida desde escaner (UI) + carga de categorias

[33mcommit b6cba0c15b1c3aa848852d460c2bdcfc600aaa4f[m[33m ([m[1;33mtag: [m[1;33mv0.3.0[m[33m, [m[1;32mfeature/packs[m[33m)[m
Merge: cb875a0 54dea38
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Wed Nov 12 20:12:00 2025 -0600

    Merge feature: inventory-scan

[33mcommit 54dea38ed7cfd8e67d1b79911fa02acf135fad56[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Wed Nov 12 19:56:07 2025 -0600

    feat(inventory-scan): ajuste por barcode (IN/OUT, delta) + alta rápida + CORS + scanner con auto-decremento y beep
    
    • Endpoint POST /api/products/adjust-by-barcode con op/qty y delta
    • Alta rápida si no existe (name, categoryId, price) con BigDecimal en price
    • CORS abierto para ngrok/localhost
    • Scanner web: antirrebote, linterna, beep y auto-decremento por barcode

[33mcommit 78be95f505ea4219d24d16f3fcec085a896e9c12[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Tue Nov 11 18:39:28 2025 -0600

    chore: bump to 0.3.0-SANPSHOT

[33mcommit cb875a0d6f173f0dc0a569b1825d26b28b9e9962[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Tue Nov 11 18:24:45 2025 -0600

    Docs: README inicial

[33mcommit 81f676b3188b71bdd0e53d8cb287898735d3ea59[m[33m ([m[1;33mtag: [m[1;33mv0.2.0[m[33m)[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Tue Nov 11 18:16:28 2025 -0600

    Fix: ajustes menores en CategoryController

[33mcommit 6d45180379e65c237d4b2ec952d18ae8ff62491f[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Mon Nov 10 19:46:06 2025 -0600

    Scanner movil (ZXing), DTOs de producto, endpoints, data.sql, low-stock & decrement

[33mcommit 01ff604ff1c979da04b930b28248ce494d93df28[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Sun Nov 9 17:03:13 2025 -0600

    chore(git): limpia .gitignore y versiona Maven Wrapper

[33mcommit 513fcd52bc2b625ea1f6046a50166b503e30e740[m
Author: Guadalupe Adrian Rosas Hinojosa <guadalupe.rhinojosa@alumnos.udg.mx>
Date:   Sun Nov 9 11:05:07 2025 -0600

    chore: Init pos-lite from Spring Initializr
