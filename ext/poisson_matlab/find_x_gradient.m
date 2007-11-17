function [Ix] = find_x_gradient(I,Nx)

Ix = [I(:,2:Nx) I(:,1)] - I;
