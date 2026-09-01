#!/bin/bash

export OLLAMA_API_BASE=http://localhost:11434

run_task() {
    local prompt="$1"
    echo "=========================================="
    echo "Ejecutando tarea: $prompt"
    echo "=========================================="

    for i in {1..3}; do
        timeout 600s aider \
            --model ollama/qwen2.5-coder:7b \
            --auto-test \
            --test-cmd "./mvnw test" \
            --yes-always \
            --auto-commits \
            --message "$prompt"

        if [ $? -eq 0 ]; then
            echo "✅ Tarea completada con éxito."
            git push origin main || true
            return 0
        else
            echo "⚠️ Intento $i falló o dio timeout. Reintentando..."
            sleep 5
        fi
    done
    echo "❌ No se pudo completar la tarea tras 3 intentos. Saltando a la siguiente..."
}

# Lista de tareas nocturnas
run_task "Agrega validaciones Jakarta (@NotNull, @Positive) a los DTOs OpenCashRequestDto y CloseCashRequestDTO."
run_task "Crea o actualiza las pruebas unitarias en CashSessionServiceTest para verificar openSession, closeSession e historias de error con Mockito hasta que pasen todas."
run_task "Crea un GlobalExceptionHandler en el paquete exception para responder con HTTP 400 Bad Request cuando ocurra IllegalStateException o IllegalArgumentException."
run_task "Crea las pruebas en CashSessionControllerTest cubriendo los endpoints POST /api/v1/cash-sessions/open y POST /api/v1/cash-sessions/{id}/close."

echo "🎉 ¡Trabajo nocturno completado!"
