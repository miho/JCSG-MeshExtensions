--
-- optimize.lua
--
-- Copyright 2016-2017 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without modification, are
-- permitted provided that the following conditions are met:
--
--    1. Redistributions of source code must retain the above copyright notice, this list of
--       conditions and the following disclaimer.
--
--    2. Redistributions in binary form must reproduce the above copyright notice, this list
--       of conditions and the following disclaimer in the documentation and/or other materials
--       provided with the distribution.
--
-- THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
-- WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
-- FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
-- CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
-- CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
-- ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
-- NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
-- ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--
-- The views and conclusions contained in the software and documentation are those of the
-- authors and should not be interpreted as representing official policies, either expressed
-- or implied, of Michael Hoffer <info@michaelhoffer.de>.
--
-- Date:   25.12.16
-- Time:   19:30
-- Author: Michael Hoffer <info@michaelhoffer.de>
--

-- script parameters
fileName            = $fileName$
removeDoublesTOL    = $removeDoublesTOL$
resolveTOL          = $resolveTOL$



-- check whether boundery edges exist
function hasBoundaryEdges(meshP)

    SelectBoundaryEdges(meshP)
    local vec_center = Vec3d()
    hasSelection = GetSelectionCenter(meshP,vec_center)

    return hasSelection
end

local mesh = Mesh()
print("> loading "..fileName)
if LoadMesh(mesh, fileName)==false then
    print(" -> ERROR while loading file.")
end

SelectAll(mesh)

numRemoved = RemoveDoubles(mesh, removeDoublesTOL)

print("> removed "..numRemoved.." doubles with TOL "..removeDoublesTOL)

ClearSelection(mesh)

local counter = 0;
local maxIter = 10;
local tolInc = (resolveTOL-removeDoublesTOL)/maxIter

print("> fixing mesh (remove bnd-edges)")
while hasBoundaryEdges(mesh) and counter < maxIter do
    local currentTol = removeDoublesTOL + tolInc*counter
    print(" -> attempt #"..counter.." with TOL "..currentTol)
    ResolveEdgeIntersection(mesh, currentTol)

    SelectAll(mesh)

    numRemoved = RemoveDoubles(mesh, currentTol)

    print(" -> removed "..numRemoved.." doubles with TOL "..currentTol)

    ClearSelection(mesh)
    counter=counter+1
end

if counter >= maxIter then
    print(" -> ERROR while fixing mesh")
    do return 1 end -- quit with exit code
end