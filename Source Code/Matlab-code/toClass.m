function [ allMat ] = toClass( data,trcol )
%UNTITLED6 Summary of this function goes here
%   Detailed explanation goes here
allMat = data;
target=data(:,trcol);
row = size(data,1);

for i = 1:row
 if  target(i,1) > 0 && target(i,1) <= 10.0
             target(i,1) = 1;
 end
 if  target(i,1) > 10 && target(i,1) <= 35.0
             target(i,1) = 2;
 end
 if  target(i,1) > 35 && target(i,1) <= 90.0
             target(i,1) = 3; 
 end
 if  target(i,1) > 90.0 
            target(i,1) = 4;  
 end

 end
allMat(:,trcol)=target;

end

