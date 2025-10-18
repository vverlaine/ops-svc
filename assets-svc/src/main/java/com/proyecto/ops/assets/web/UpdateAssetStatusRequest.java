package com.proyecto.ops.assets.web;

import com.proyecto.ops.assets.model.AssetStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateAssetStatusRequest(@NotNull AssetStatus status) {}