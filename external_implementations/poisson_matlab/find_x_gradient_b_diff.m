function [Ix] = find_x_gradient_b_diff(I,Nx)
I = double(I);
Ix = I - [I(:,2:Nx) I(:,1)];

