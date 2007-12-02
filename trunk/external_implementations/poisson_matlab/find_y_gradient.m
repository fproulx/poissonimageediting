function [Iy] = find_y_gradient(I,Ny)

Iy = [I(2:Ny,:); I(1,:)] - I;