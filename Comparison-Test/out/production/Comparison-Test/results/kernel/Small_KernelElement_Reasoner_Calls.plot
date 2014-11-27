set terminal png size 1024,768
set output "Small_KernelElement_Reasoner_Calls.png"
set title "Estratégias para a Construção de Elementos do Conjunto Kernel"
set xlabel "Tamanho da Ontologia (# de axiomas)"
set ylabel "# de chamadas ao mecanismo de inferências"
set key below

plot 'Small_KernelElement.dat' using 1:26 with lines title 'Expansão Clássica, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:27 with lines title 'Expansão Clássica, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:28 with lines title 'Expansão Clássica, Contração Divisão e Conquista' smooth csplines,\
'Small_KernelElement.dat' using 1:29 with lines title 'Expansão Sintática, Contração Clássica' smooth csplines,\
'Small_KernelElement.dat' using 1:30 with lines title 'Expansão Sintática, Contração Janelas Deslizantes' smooth csplines,\
'Small_KernelElement.dat' using 1:31 with lines title 'Expansão Sintática, Contração Divisão e Conquista' smooth csplines
