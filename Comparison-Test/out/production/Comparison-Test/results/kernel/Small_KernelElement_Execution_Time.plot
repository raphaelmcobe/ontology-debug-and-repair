set terminal png size 1024,768
set output "Small_KernelElement_Execution_Time.png"
set title "Estratégias para a Construção de Elementos do Conjunto Kernel - Tempo Total da Execução"
set xlabel "Tamanho da Ontologia (# de axiomas)"
set ylabel "Tempo (ms)"
set key below

plot 'Small_KernelElement.dat' using 1:8 with lines title 'Expansão Clássica, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:9 with lines title 'Expansão Clássica, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:10 with lines title 'Expansão Clássica, Contração Divisão e Conquista' smooth csplines,\
'Small_KernelElement.dat' using 1:11 with lines title 'Expansão Sintática, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:12 with lines title 'Expansão Sintática, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:13 with lines title 'Expansão Sintática, Contração Divisão e Conquista' smooth csplines
