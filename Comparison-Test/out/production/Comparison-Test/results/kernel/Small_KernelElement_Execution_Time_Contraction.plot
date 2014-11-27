set terminal png size 1024,768
set output "Small_KernelElement_Execution_Time_Contraction.png"
set title "Estratégias para a Construção de Elementos do Conjunto Kernel - Tempo para Contração"
set xlabel "Tamanho da Ontologia (# de axiomas)"
set ylabel "Tempo (ms)"
set key below

plot 'Small_KernelElement.dat' using 1:20 with lines title 'Expansão Clássica, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:21 with lines title 'Expansão Clássica, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:22 with lines title 'Expansão Clássica, Contração Divisão e Conquista' smooth csplines,\
'Small_KernelElement.dat' using 1:23 with lines title 'Expansão Sintática, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:24 with lines title 'Expansão Sintática, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:25 with lines title 'Expansão Sintática, Contração Divisão e Conquista' smooth csplines
