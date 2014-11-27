set terminal png size 1024,768
set output "Small_KernelElement_Execution_Time_Expansion.png"
set title "Estratégias para a Construção de Elementos do Conjunto Kernel - Tempo para Expansão"
set xlabel "Tamanho da Ontologia (# de axiomas)"
set ylabel "Tempo (ms)"
set key below

plot 'Small_KernelElement.dat' using 1:14 with lines title 'Expansão Clássica, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:15 with lines title 'Expansão Clássica, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:16 with lines title 'Expansão Clássica, Contração Divisão e Conquista' smooth csplines,\
'Small_KernelElement.dat' using 1:17 with lines title 'Expansão Sintática, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:18 with lines title 'Expansão Sintática, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:19 with lines title 'Expansão Sintática, Contração Divisão e Conquista' smooth csplines
