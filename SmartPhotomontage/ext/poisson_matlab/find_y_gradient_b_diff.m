function [Iy] = find_y_gradient_b_diff(I,Ny)
I = double(I);
Iy = I - [I(2:Ny,:); I(1,:)];